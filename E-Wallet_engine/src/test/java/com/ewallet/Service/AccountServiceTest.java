package com.ewallet.Service;

import com.ewallet.dto.AccountResponse;
import com.ewallet.dto.OpenAccountRequest;
import com.ewallet.entity.Account;
import com.ewallet.entity.Money;
import com.ewallet.enumeration.AccountStatus;
import com.ewallet.repository.AccountRepository;
import com.ewallet.service.AccountService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.ewallet.service.AccountNumberGenerator;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountNumberGenerator accountNumberGenerator;

    @InjectMocks
    private AccountService accountService;

    @Test
    void shouldOpenAccountWithZeroBalance() {

        // Arrange

        Currency mad = Currency.getInstance("MAD");

        OpenAccountRequest request = mock(OpenAccountRequest.class);

        when(request.getOwnerName()).thenReturn("Ayoub");
        when(request.getCurrency()).thenReturn(mad);

        when(accountNumberGenerator.generate())
                .thenReturn("EW-123456789012");

//        when(accountRepository.save(any(Account.class))).thenAnswer(InvocationOnMock::getArguments);
        when(accountRepository.save(any(Account.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act

        AccountResponse result = accountService.openAccount(request);

        // Assert
//
//        @BeforeEach
//       void setUp(){
//            System.out.println("test not Null");
//        }
        assertNotNull(result);

        assertEquals("Ayoub", result.getOwnerName());

        assertEquals("EW-123456789012", result.getAccountNumber());

        assertEquals(AccountStatus.ACTIVE, result.getStatus());

        assertEquals(Money.zero(mad), result.getBalance());

        verify(accountRepository).save(any(Account.class));

        verify(accountNumberGenerator).generate();

    }
}