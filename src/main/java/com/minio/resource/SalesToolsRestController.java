package com.minio.resource;

import com.minio.constants.UrlAgreements;
import com.minio.dto.*;
import com.minio.executor.*;
import com.minio.model.FilterFieldType;
import com.minio.model.FilterOperator;
import com.minio.model.LogicalOperator;
import com.minio.service.MarketingTargetFilterService;
import com.minio.service.FilterEvaluationService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Arrays;
import java.util.Optional;

@Path(UrlAgreements.PATH_SALES_TOOLS)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SalesToolsRestController {

    @Inject
    MarketingTargetFilterService filterService;
    
    @Inject
    FilterEvaluationService evaluationService;

    @GET
    @Path(UrlAgreements.PATH_SALES_TOOLS_NOTIFICATION_STATUS)
    public Response getMarketingNotificationStatus() {
        try {
            List<NotificationStatusDto> statuses = Arrays.asList(
                new NotificationStatusDto("CREATED", "Создано"),
                new NotificationStatusDto("SENT", "Отправлено"),
                new NotificationStatusDto("DELIVERED", "Доставлено"),
                new NotificationStatusDto("FAILED", "Ошибка"),
                new NotificationStatusDto("CANCELLED", "Отменено")
            );
            return Response.ok(statuses).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Error retrieving notification statuses: " + e.getMessage())
                .build();
        }
    }

    @PUT
    @Path(UrlAgreements.PATH_SALES_TOOLS_NOTIFICATION + UrlAgreements.OPERATION_CHANGE_STATUS)
    public Response changeStatusMarketingNotification(
            @QueryParam("id") Long id,
            ChangeNotificationStatusDto dto
    ) {
        try {
            // TODO: Реализовать через экзекютор когда будет готов сервис уведомлений
            return Response.ok().build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Error changing notification status: " + e.getMessage())
                .build();
        }
    }

    @POST
    @Path(UrlAgreements.PATH_SALES_TOOLS_NOTIFICATION)
    public Response insertMarketingNotification(MarketingNotificationDto dto) {
        try {
            // TODO: Реализовать через экзекютор когда будет готов сервис уведомлений
            return Response.status(Response.Status.CREATED).entity(1L).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Error creating notification: " + e.getMessage())
                .build();
        }
    }

    @POST
    @Path(UrlAgreements.PATH_SALES_TOOLS_TARGET + UrlAgreements.OPERATION_LIST)
    public Response uploadListMarketingTarget(MarketingTargetDto dto) {
        try {
            // TODO: Реализовать через экзекютор когда будет готов сервис целей
            return Response.status(Response.Status.CREATED).entity(1L).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Error uploading target list: " + e.getMessage())
                .build();
        }
    }


    // ================ CRUD операции для фильтров через экзекюторы ================
    
    @GET
    @Path(UrlAgreements.PATH_SALES_TOOLS_FILTERS)
    public Response getAllFilters() {
        try {
            List<MarketingTargetFilterDto> filters = new GetAllFiltersExecutor(filterService).execute();
            return Response.ok(filters).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Error retrieving filters: " + e.getMessage())
                .build();
        }
    }
    
    @GET
    @Path(UrlAgreements.PATH_SALES_TOOLS_FILTERS + UrlAgreements.OPERATION_ID_PARAM)
    public Response getFilterById(@PathParam("id") Long id) {
        try {
            Optional<MarketingTargetFilterDto> filter = new GetFilterByIdExecutor(filterService, id).execute();
            if (filter.isPresent()) {
                return Response.ok(filter.get()).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                    .entity("Filter not found with id: " + id)
                    .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Error retrieving filter: " + e.getMessage())
                .build();
        }
    }
    
    @POST
    @Path(UrlAgreements.PATH_SALES_TOOLS_FILTERS)
    public Response createFilter(MarketingTargetFilterDto filterDto) {
        try {
            if (filterDto.getFilterName() == null || filterDto.getFilterName().trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Filter name is required")
                    .build();
            }
            
            if (filterDto.getMarketingTargetId() == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Marketing target ID is required")
                    .build();
            }
            
            MarketingTargetFilterDto createdFilter = new CreateFilterExecutor(filterService, filterDto).execute();
            return Response.status(Response.Status.CREATED).entity(createdFilter).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("Error creating filter: " + e.getMessage())
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Error creating filter: " + e.getMessage())
                .build();
        }
    }
    
    @PUT
    @Path(UrlAgreements.PATH_SALES_TOOLS_FILTERS + UrlAgreements.OPERATION_ID_PARAM)
    public Response updateFilter(@PathParam("id") Long id, MarketingTargetFilterDto filterDto) {
        try {
            if (filterDto.getFilterName() == null || filterDto.getFilterName().trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Filter name is required")
                    .build();
            }
            
            MarketingTargetFilterDto updatedFilter = new UpdateFilterExecutor(filterService, id, filterDto).execute();
            if (updatedFilter != null) {
                return Response.ok(updatedFilter).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                    .entity("Filter not found with id: " + id)
                    .build();
            }
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("Error updating filter: " + e.getMessage())
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Error updating filter: " + e.getMessage())
                .build();
        }
    }
    
    @DELETE
    @Path(UrlAgreements.PATH_SALES_TOOLS_FILTERS + UrlAgreements.OPERATION_ID_PARAM)
    public Response deleteFilter(@PathParam("id") Long id) {
        try {
            new DeleteFilterExecutor(filterService, id).execute();
            return Response.noContent().build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Error deleting filter: " + e.getMessage())
                .build();
        }
    }
    
    @GET
    @Path(UrlAgreements.PATH_SALES_TOOLS_FILTERS_FIELD_TYPES)
    public Response getFilterFieldTypes() {
        try {
            List<FilterFieldType> fieldTypes = Arrays.asList(FilterFieldType.values());
            return Response.ok(fieldTypes).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Error retrieving field types: " + e.getMessage())
                .build();
        }
    }
    
    @GET
    @Path(UrlAgreements.PATH_SALES_TOOLS_FILTERS_OPERATORS)
    public Response getFilterOperators() {
        try {
            List<FilterOperator> operators = Arrays.asList(FilterOperator.values());
            return Response.ok(operators).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Error retrieving operators: " + e.getMessage())
                .build();
        }
    }
    
    @GET
    @Path(UrlAgreements.PATH_SALES_TOOLS_FILTERS_LOGICAL_OPERATORS)
    public Response getLogicalOperators() {
        try {
            List<LogicalOperator> logicalOperators = Arrays.asList(LogicalOperator.values());
            return Response.ok(logicalOperators).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Error retrieving logical operators: " + e.getMessage())
                .build();
        }
    }
    
    @POST
    @Path(UrlAgreements.PATH_SALES_TOOLS_FILTERS_EVALUATE + UrlAgreements.OPERATION_ID_PARAM)
    public Response evaluateFilter(@PathParam("id") Long filterId, UserRequestDto userRequest) {
        try {
            boolean matches = new EvaluateFilterExecutor(evaluationService, filterId, userRequest).execute();
            return Response.ok()
                .entity("{\"filterId\": " + filterId + ", \"matches\": " + matches + "}")
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"error\": \"Error evaluating filter: " + e.getMessage() + "\"}")
                .build();
        }
    }
    
    @POST
    @Path(UrlAgreements.PATH_SALES_TOOLS_FILTERS_EVALUATE)
    public Response evaluateFilterDirect(EvaluationRequestDto request) {
        try {
            boolean matches = evaluationService.evaluateFilter(request.getFilter(), request.getUserRequest());
            return Response.ok()
                .entity("{\"matches\": " + matches + "}")
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"error\": \"Error evaluating filter: " + e.getMessage() + "\"}")
                .build();
        }
    }
    
    @POST
    @Path(UrlAgreements.PATH_SALES_TOOLS_FILTERS_VALIDATE)
    public Response validateFilterStructure(MarketingTargetFilterDto filterDto) {
        try {
            filterService.validateFilterStructure(filterDto);
            return Response.ok()
                .entity("{\"valid\": true, \"message\": \"Filter structure is valid\"}")
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"valid\": false, \"message\": \"" + e.getMessage() + "\"}")
                .build();
        }
    }
}
