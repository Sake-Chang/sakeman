package com.sakeman.config;

import io.sentry.Sentry;
import org.springframework.stereotype.Component;
import javax.annotation.PreDestroy;

@Component
public class SentryShutdown {
    @PreDestroy
    public void cleanUp() {
        Sentry.close();  // Springアプリケーションのシャットダウン時にSentryを閉じる
    }
}
