package cc.leedaud.aspect;

import cc.leedaud.annotation.RateLimit;
import cc.leedaud.config.RateLimitConfiguration;
import cc.leedaud.exception.BlockedException;
import cc.leedaud.utils.IpUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Duration;

/**
 * 闄愭祦鍒囬潰锛屽鐞?@RateLimit 娉ㄨВ
 * 鍩轰簬Bucket4j浠ょ墝妗剁畻娉曞疄鐜版帴鍙ｇ骇闄愭祦
 */
@Aspect
@Component
@Slf4j
public class RateLimitAspect {

    @Autowired
    private RateLimitConfiguration rateLimitConfig;

    /**
     * 鐜粫閫氱煡锛氭嫤鎴甫鏈?@RateLimit 娉ㄨВ鐨勬柟娉?     */
    @Around("@annotation(rateLimit)")
    public Object around(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
        String key = buildRateLimitKey(joinPoint, rateLimit);
        Duration duration = Duration.of(rateLimit.timeWindow(), rateLimit.timeUnit().toChronoUnit());

        boolean allowed = rateLimitConfig.tryConsume(
                key,
                rateLimit.burstCapacity(),
                (long) rateLimit.tokens(),
                duration
        );

        if (allowed) {
            return joinPoint.proceed();
        } else {
            log.warn("闄愭祦瑙﹀彂: key={}, type={}, message={}", key, rateLimit.type(), rateLimit.message());
            // 403灏佺寮傚父
            throw new BlockedException(rateLimit.message());
        }
    }

    /**
     * 鏋勫缓闄愭祦Key
     */
    private String buildRateLimitKey(ProceedingJoinPoint joinPoint, RateLimit rateLimit) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = signature.getDeclaringType().getSimpleName();
        String methodName = signature.getMethod().getName();
        String endpointKey = className + "." + methodName;

        StringBuilder key = new StringBuilder("rate_limit:");

        switch (rateLimit.type()) {
            case IP:
                String ip = getClientIp();
                key.append("ip:").append(endpointKey).append(":").append(ip);
                break;

            case ENDPOINT:
                key.append("endpoint:").append(endpointKey);
                break;

            case GLOBAL:
                key.append("global");
                break;
        }

        return key.toString();
    }

    /**
     * 鑾峰彇瀹㈡埛绔疘P鍦板潃
     */
    private String getClientIp() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            return IpUtil.getClientIp(request);
        }
        return "unknown";
    }
}

