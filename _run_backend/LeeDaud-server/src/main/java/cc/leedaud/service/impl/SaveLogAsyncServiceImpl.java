package cc.leedaud.service.impl;

import cc.leedaud.annotation.OperationLog;
import cc.leedaud.constant.StatusConstant;
import cc.leedaud.context.BaseContext;
import cc.leedaud.entity.OperationLogs;
import cc.leedaud.service.SaveLogAsyncService;
import cc.leedaud.service.OperationLogService;
import com.alibaba.fastjson.JSON;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class SaveLogAsyncServiceImpl implements SaveLogAsyncService {

    @Autowired
    private OperationLogService operationLogService;

    /**
     * SpEL琛ㄨ揪寮忚В鏋愬櫒
     */
    private final ExpressionParser parser = new SpelExpressionParser();

    /**
     * 鍙傛暟鍚嶅彂鐜板櫒
     */
    private final ParameterNameDiscoverer discoverer = new DefaultParameterNameDiscoverer();

    /**
     * 寮傛淇濆瓨鏃ュ織
     * @param joinPoint
     * @param result
     * @param error
     * @param operationLog
     */
    @Async("taskExecutor")
    public void saveLogAsync(JoinPoint joinPoint, Object result,
                             Throwable error, OperationLog operationLog) {
        OperationLogs operationLogs = new OperationLogs();

        try {
            // 淇濆瓨鍩烘湰淇℃伅
            operationLogs.setOperationType(operationLog.value().toString());
            operationLogs.setOperationTarget(operationLog.target());
            operationLogs.setOperationTime(LocalDateTime.now());

            // 璁板綍鎿嶄綔缁撴灉
            if (error != null) {
                operationLogs.setResult(StatusConstant.DISABLE); // 澶辫触
                operationLogs.setErrorMessage(getErrorMessage(error));
            } else {
                operationLogs.setResult(StatusConstant.ENABLE); // 鎴愬姛
            }

            // 璁板綍鎿嶄綔鐢ㄦ埛
            Long adminId = BaseContext.getCurrentId();
            if (adminId != null) {
                operationLogs.setAdminId(adminId);
            }

            // 鑾峰彇鐩爣ID,浠嶴pEL琛ㄨ揪寮忎腑瑙ｆ瀽
            if (!operationLog.targetId().isEmpty()) {
                Integer targetId = parseTargetId(joinPoint, operationLog.targetId());
                if (targetId != null) {
                    operationLogs.setTargetId(targetId);
                }
            }

            // 璁板綍鎿嶄綔鏁版嵁
            if (operationLog.saveData()) {
                String operateData = buildOperateData(joinPoint);
                if (operateData != null && operateData.length() > 5000) {
                    operateData = operateData.substring(0, 5000) + "...";
                }
                operationLogs.setOperateData(operateData);
            }

            // 淇濆瓨鍒版暟鎹簱
            operationLogService.save(operationLogs);
        } catch (Exception e) {
            log.error("淇濆瓨鎿嶄綔鏃ュ織澶辫触", e);
        }
    }

    /**
     * 鑾峰彇閿欒淇℃伅
     */
    private String getErrorMessage(Throwable error) {
        if (error == null) {
            return null;
        }

        // 鏋勫缓閿欒淇℃伅
        StringBuilder sb = new StringBuilder();
        sb.append(error.getClass().getSimpleName())
                .append(": ")
                .append(error.getMessage());

        // 闄愬埗閿欒淇℃伅闀垮害
        String message = sb.toString();
        if (message.length() > 1000) {
            message = message.substring(0, 1000) + "...";
        }

        return message;
    }

    /**
     * 瑙ｆ瀽鐩爣ID锛圫pEL琛ㄨ揪寮忥級
     */
    private Integer parseTargetId(JoinPoint joinPoint, String targetIdExpression) {
        try {
            if (targetIdExpression == null || targetIdExpression.isEmpty()) {
                return null;
            }

            // 鍒涘缓SpEL涓婁笅鏂?            StandardEvaluationContext context = new StandardEvaluationContext();

            // 璁剧疆鍙傛暟
            Object[] args = joinPoint.getArgs();
            String[] paramNames = discoverer.getParameterNames(
                    ((MethodSignature) joinPoint.getSignature()).getMethod()
            );

            if (paramNames != null) {
                for (int i = 0; i < paramNames.length; i++) {
                    context.setVariable(paramNames[i], args[i]);
                }
            }

            // 璁剧疆鏂规硶鍙傛暟锛坧0, p1, p2...锛?            for (int i = 0; i < args.length; i++) {
                context.setVariable("p" + i, args[i]);
            }

            // 瑙ｆ瀽琛ㄨ揪寮?            Expression expression = parser.parseExpression(targetIdExpression);
            Object value = expression.getValue(context);

            // 濡傛灉鏄泦鍚堢被鍨嬶紙鎵归噺鎿嶄綔 #ids锛夛紝鍙栫涓€涓厓绱?            if (value instanceof java.util.Collection<?> col) {
                if (col.isEmpty()) return null;
                value = col.iterator().next();
            }

            if (value instanceof Number) {
                return ((Number) value).intValue();
            } else if (value != null) {
                try {
                    return Integer.parseInt(value.toString());
                } catch (NumberFormatException e) {
                    log.warn("鐩爣ID鏃犳硶杞崲涓烘暣鏁? {}", value);
                    return null;
                }
            }

            return null;

        } catch (Exception e) {
            log.warn("瑙ｆ瀽鐩爣ID琛ㄨ揪寮忓け璐? {}", targetIdExpression, e);
            return null;
        }
    }

    /**
     * 鏋勫缓鎿嶄綔鏁版嵁
     */
    private String buildOperateData(JoinPoint joinPoint) {
        try {
            Object[] args = joinPoint.getArgs();
            if (args == null || args.length == 0) {
                return null;
            }

            // 鏋勫缓鍙傛暟Map
            Map<String, Object> params = new HashMap<>();
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            String[] paramNames = discoverer.getParameterNames(signature.getMethod());

            for (int i = 0; i < args.length; i++) {
                String paramName = (paramNames != null && i < paramNames.length)
                        ? paramNames[i] : "arg" + i;

                // 璺宠繃涓嶅彲搴忓垪鍖栫殑 Servlet / IO 绫诲瀷鍙傛暟
                if (args[i] instanceof ServletRequest
                        || args[i] instanceof ServletResponse
                        || args[i] instanceof MultipartFile) {
                    continue;
                }

                // 鏁忔劅鍙傛暟杩囨护
                Object paramValue = filterSensitiveParam(paramName, args[i]);
                params.put(paramName, paramValue);
            }

            // 杞崲涓篔SON锛堣繃婊ゆ晱鎰熷瓧娈碉級
            return JSON.toJSONString(params);

        } catch (Exception e) {
            log.warn("鏋勫缓鎿嶄綔鏁版嵁澶辫触", e);
            return null;
        }
    }

    /**
     * 杩囨护鏁忔劅鍙傛暟
     */
    private Object filterSensitiveParam(String paramName, Object paramValue) {
        if (paramValue == null) {
            return null;
        }

        // 妫€鏌ュ弬鏁板悕鏄惁鍖呭惈鏁忔劅璇?        String lowerParamName = paramName.toLowerCase();
        if (lowerParamName.contains("password") ||
                lowerParamName.contains("pwd") ||
                lowerParamName.contains("token") ||
                lowerParamName.contains("salt") ||
                lowerParamName.contains("secret")) {
            return "***";
        }

        return paramValue;
    }
}

