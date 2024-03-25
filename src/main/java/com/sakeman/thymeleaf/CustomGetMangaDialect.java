package com.sakeman.thymeleaf;

import java.util.HashSet;
import java.util.Set;

import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.dialect.IExpressionObjectDialect;
import org.thymeleaf.expression.IExpressionObjectFactory;

import com.sakeman.repository.ReadStatusRepository;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;


public class CustomGetMangaDialect implements IExpressionObjectDialect {

    private static final String EXPRESSION_NAME = "getReadManga";
    private static final Set<String> ALL_EXPRESSION_SET = new HashSet<String>() {
        {add(EXPRESSION_NAME);}
    };

    private final ReadStatusRepository rsRepository;

    public CustomGetMangaDialect(ReadStatusRepository rsRepository) {
        this.rsRepository = rsRepository;
    }


    @Override
    public String getName() {
        return "CustomGetMangaDialect";
    }

    @Override
    public IExpressionObjectFactory getExpressionObjectFactory() {
        return new IExpressionObjectFactory() {
            @Override
            public boolean isCacheable(String expressionObjectName) {
                return false;
            }
            @Override
            public Set<String> getAllExpressionObjectNames() {
                return ALL_EXPRESSION_SET;
            }
            @Override
            public Object buildObject(IExpressionContext context, String expressionObjectName) {
                switch (expressionObjectName) {
                    case EXPRESSION_NAME:
                        return new GetReadManga(rsRepository);
                    default:
                        return null;
                }
            }
        };
    }

}
