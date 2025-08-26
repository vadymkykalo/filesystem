package com.minio.resource;

import com.minio.dto.MarketingTargetFilterDto;
import com.minio.dto.UserRequestDto;
import com.minio.dto.EvaluationRequestDto;
import com.minio.model.FilterFieldType;
import com.minio.model.FilterOperator;
import com.minio.model.LogicalOperator;
import com.minio.service.MarketingTargetFilterService;
import com.minio.service.FilterEvaluationService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Path("/api/marketing-target-filters")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MarketingTargetFilterResource {
    
    @Inject
    MarketingTargetFilterService filterService;
    
    @Inject
    FilterEvaluationService evaluationService;
    
    @GET
    public Response getAllFilters() {
        try {
            List<MarketingTargetFilterDto> filters = filterService.getAllFilters();
            return Response.ok(filters).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Error retrieving filters: " + e.getMessage())
                .build();
        }
    }
    
    @GET
    @Path("/{id}")
    public Response getFilterById(@PathParam("id") Long id) {
        try {
            Optional<MarketingTargetFilterDto> filter = filterService.getFilterById(id);
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
    
    @GET
    @Path("/by-target/{marketingTargetId}")
    public Response getFiltersByMarketingTargetId(@PathParam("marketingTargetId") Long marketingTargetId) {
        try {
            List<MarketingTargetFilterDto> filters = filterService.getFiltersByMarketingTargetId(marketingTargetId);
            return Response.ok(filters).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Error retrieving filters: " + e.getMessage())
                .build();
        }
    }
    
    @POST
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
            
            MarketingTargetFilterDto createdFilter = filterService.createFilter(filterDto);
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
    @Path("/{id}")
    public Response updateFilter(@PathParam("id") Long id, MarketingTargetFilterDto filterDto) {
        try {
            if (filterDto.getFilterName() == null || filterDto.getFilterName().trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Filter name is required")
                    .build();
            }
            
            MarketingTargetFilterDto updatedFilter = filterService.updateFilter(id, filterDto);
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
    @Path("/{id}")
    public Response deleteFilter(@PathParam("id") Long id) {
        try {
            filterService.deleteFilter(id);
            return Response.noContent().build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Error deleting filter: " + e.getMessage())
                .build();
        }
    }
    
    @GET
    @Path("/field-types")
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
    @Path("/operators")
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
    @Path("/logical-operators")
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
    
    /**
     * Проверить соответствие пользователя фильтру
     */
    @POST
    @Path("/evaluate/{filterId}")
    public Response evaluateFilter(@PathParam("filterId") Long filterId, UserRequestDto userRequest) {
        try {
            boolean matches = evaluationService.evaluateFilter(filterId, userRequest);
            return Response.ok()
                .entity("{\"filterId\": " + filterId + ", \"matches\": " + matches + "}")
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"error\": \"Error evaluating filter: " + e.getMessage() + "\"}")
                .build();
        }
    }
    
    /**
     * Проверить соответствие пользователя фильтру (с передачей фильтра в теле запроса)
     */
    @POST
    @Path("/evaluate")
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
    
    /**
     * Validate filter structure
     */
    @POST
    @Path("/validate")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
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
