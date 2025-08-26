package com.minio.executor;

/**
 * Базовый абстрактный класс для всех экзекюторов
 */
public abstract class AWSExecutor<T> {
    
    /**
     * Выполнить операцию
     * @return результат выполнения операции
     * @throws Exception в случае ошибки выполнения
     */
    public abstract T execute() throws Exception;
}
