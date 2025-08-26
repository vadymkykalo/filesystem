package com.minio.service;

import com.minio.dto.NotificationStatusDto;
import com.minio.dto.ChangeNotificationStatusDto;
import com.minio.dto.MarketingTargetDto;
import com.minio.dto.MarketingNotificationDto;
import java.util.List;

/**
 * Интерфейс сервиса для работы с инструментами продаж и маркетинговыми уведомлениями
 */
public interface ISalesToolsRestService {
    
    /**
     * Получить список статусов маркетинговых уведомлений
     */
    List<NotificationStatusDto> getMarketingNotificationStatus();
    
    /**
     * Изменить статус маркетингового уведомления
     */
    void changeStatusMarketingNotification(Long id, ChangeNotificationStatusDto dto);
    
    /**
     * Вставить новое маркетинговое уведомление
     */
    Long insertMarketingNotification(MarketingNotificationDto dto);
    
    /**
     * Загрузить список маркетинговых целей
     */
    Long uploadListMarketingTarget(MarketingTargetDto dto);

}
