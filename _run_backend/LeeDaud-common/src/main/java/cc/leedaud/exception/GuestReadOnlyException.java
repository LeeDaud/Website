package cc.leedaud.exception;

/**
 * 娓稿鍙寮傚父 - 娓稿璐﹀彿灏濊瘯鎵ц鍐欐搷浣滄椂鎶涘嚭
 */
public class GuestReadOnlyException extends RuntimeException {
    public GuestReadOnlyException() {
    }
    public GuestReadOnlyException(String msg) {
        super(msg);
    }
}

