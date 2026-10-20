package com.ewallet.controller;

import com.ewallet.dto.AccountResponse;
import com.ewallet.dto.OpenAccountRequest;
import com.ewallet.dto.StatementLineDTO;
import com.ewallet.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(
            @Valid @RequestBody OpenAccountRequest openAccountRequest
    ) {

        AccountResponse resulta = accountService.openAccount(openAccountRequest);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(resulta);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> getAccount(@Valid @PathVariable UUID id) {
        AccountResponse resulta = accountService.getAccount(id);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(resulta);
    }

    @GetMapping("/{id}/statement")
    public Page<StatementLineDTO> getStatement(
            @PathVariable UUID id,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return accountService.getStatement(id, pageable);
    }
}
