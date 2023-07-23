package ru.netology.sql.test;

import org.junit.jupiter.api.Test;
import ru.netology.sql.data.APIHelper;
import ru.netology.sql.data.DataHelper;
import ru.netology.sql.data.SQLHelper;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class APISQLTest {
    @Test
    public void validTransferFromFirstCardToSecondCard() {
        var authinfo = DataHelper.getAuthInfoWithTestData();
        APIHelper.makeQueryToLogin(authinfo, 200);
        var verificationCode = SQLHelper.getVerificationCode();
        var verificationInfo = new DataHelper.VerificationInfo(authinfo.getLogin(), verificationCode.getCode());
        var tokenInfo = APIHelper.sendQueryToVerify(verificationInfo, 200);
        var cardsBalances = APIHelper.sendQueryToGetCardsBalances(tokenInfo.getToken(), 200);
        var firstCardBalance = cardsBalances.get(DataHelper.getFirstCardInfo().getId());
        var secondCardBalance = cardsBalances.get(DataHelper.getSecondCardInfo().getId());
        var amount = DataHelper.generateValidAmount(firstCardBalance);
        var transferInfo = new APIHelper.APITransferInfo(DataHelper.getFirstCardInfo().getNumber(), DataHelper.getSecondCardInfo().getNumber(), amount);
        APIHelper.generateQueryToTransfer(tokenInfo.getToken(), transferInfo, 200);
        cardsBalances = APIHelper.sendQueryToGetCardsBalances(tokenInfo.getToken(), 200);
        var actualFirstCardBalance = cardsBalances.get(DataHelper.getFirstCardInfo().getId());
        var actualSecondCardBalance = cardsBalances.get(DataHelper.getSecondCardInfo().getId());
        assertAll(() -> assertEquals(firstCardBalance - amount, actualFirstCardBalance),
                () -> assertEquals(secondCardBalance + amount, actualSecondCardBalance));
    }
}
