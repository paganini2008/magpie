package com.github.doodler.common.context;

import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import lombok.Data;
import lombok.experimental.UtilityClass;

/**
 * 
 * @Description: SpelUtils
 * @Author: Fred Feng
 * @Date: 17/01/2025
 * @Version 1.0.0
 */
@UtilityClass
public class SpelUtils {

    @Data
    public static class Address {
        private String city;

        public Address(String city) {
            this.city = city;
        }

        public String getCity() {
            return city;
        }
    }

    @Data
    public static class User {
        private String name;
        private Address address;

        public User(String name, Address address) {
            this.name = name;
            this.address = address;
        }

        public Address getAddress() {
            return address;
        }
    }

    public String parseAndRun(String text) {
        SpelExpressionParser parser = new SpelExpressionParser();
        Expression expression = parser.parseExpression(text);
        return expression.getValue(String.class);
    }

    public <T> T parseAndRun(Object[] args, String text, Class<T> requiredType) {
        SpelExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();
        for (int i = 0; i < args.length; i++) {
            context.setVariable("p" + i, args[i]);
        }
        Expression expression = parser.parseExpression(text);
        return expression.getValue(context, requiredType);
    }

    public static void main(String[] args) {
        User user = new User("Bob", new Address("Sydney"));
        System.out.println(parseAndRun(new Object[] {user}, "''", String.class));
    }

}
