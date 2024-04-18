package edu.java.controller;

import edu.java.controller.dto.AddLinkRequest;
import edu.java.controller.dto.ApiErrorResponse;
import edu.java.controller.dto.LinkResponse;
import edu.java.controller.dto.ListLinkResponse;
import edu.java.controller.dto.RemoveLinkRequest;
import edu.java.services.interfaces.ISubscribeService;
import edu.java.services.interfaces.ITgChatService;
import edu.java.services.metrics.CounterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ScrapperController {
    ITgChatService tgChatService;
    ISubscribeService subscribeService;
    CounterService counterService;

    public ScrapperController(
        ITgChatService tgChatService,
        ISubscribeService subscribeService,
        CounterService counterService
    ) {
        this.tgChatService = tgChatService;
        this.subscribeService = subscribeService;
        this.counterService = counterService;
    }

    @PostMapping("/tg-chat/{id}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Чат зарегистрирован"),
        @ApiResponse(responseCode = "400",
                     description = "Некорректные параметры запроса",
                     content = @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @Operation(summary = "Зарегистрировать чат")
    public ResponseEntity<Void> postTgChat(@PathVariable(name = "id") Long id) {
        tgChatService.addUser(id);
        counterService.successfulRequestsCounterIncrement();
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @DeleteMapping("/tg-chat/{id}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Чат успешно удалён"),
        @ApiResponse(responseCode = "400",
                     description = "Некорректные параметры запроса",
                     content = @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = ApiErrorResponse.class))),
        @ApiResponse(responseCode = "404",
                     description = "Чат не существует",
                     content = @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @Operation(summary = "Удалить чат")
    public ResponseEntity<Void> deleteTgChat(@PathVariable(name = "id") Long id) {
        tgChatService.remove(id);
        counterService.successfulRequestsCounterIncrement();
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @GetMapping("/links")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200",
                     description = "Ссылки успешно получены",
                     content = @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = ListLinkResponse.class))),
        @ApiResponse(responseCode = "400",
                     description = "Некорректные параметры запроса",
                     content = @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @Operation(summary = "Получить все отслеживаемые ссылки")
    public ResponseEntity<ListLinkResponse> getLinks(@RequestHeader(name = "Tg-Chat-Id") Long id) {
        ResponseEntity<ListLinkResponse> response = ResponseEntity.status(HttpStatus.OK)
            .body(new ListLinkResponse(subscribeService.getTrackedURLs(id)));
        counterService.successfulRequestsCounterIncrement();
        return response;
    }

    @PostMapping("/links")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ссылка успешно добавлена"),
        @ApiResponse(responseCode = "400",
                     description = "Некорректные параметры запроса",
                     content = @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @Operation(summary = "Добавить отслеживание ссылки")
    public ResponseEntity<LinkResponse> postLinks(
        @RequestHeader(name = "Tg-Chat-Id") Long id,
        @Valid @RequestBody AddLinkRequest addLinkRequest
    ) throws URISyntaxException {
        Long linkId = subscribeService.addTrackedURLs(id, addLinkRequest.getLink());
        ResponseEntity<LinkResponse> response =
            ResponseEntity.status(HttpStatus.OK).body(new LinkResponse(linkId, new URI(addLinkRequest.getLink())));
        counterService.successfulRequestsCounterIncrement();
        return response;
    }

    @DeleteMapping("/links")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ссылка успешно убрана"),
        @ApiResponse(responseCode = "400",
                     description = "Некорректные параметры запроса",
                     content = @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = ApiErrorResponse.class))),
        @ApiResponse(responseCode = "404",
                     description = "Ссылка не найдена",
                     content = @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @Operation(summary = "Убрать отслеживание ссылки")
    public ResponseEntity<LinkResponse> deleteLinks(
        @RequestHeader(name = "Tg-Chat-Id") Long id,
        @RequestBody @Valid RemoveLinkRequest removeLinkRequest
    ) throws URISyntaxException {
        Long linkId = subscribeService.removeTrackedURLs(id, removeLinkRequest.getLink());
        ResponseEntity<LinkResponse> response = ResponseEntity.status(HttpStatus.OK)
            .body(new LinkResponse(linkId, new URI(removeLinkRequest.getLink())));
        counterService.successfulRequestsCounterIncrement();
        return response;
    }
}
