package isv.sap.payment.fulfilmentprocess.strategy.impl.takepayment

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.core.model.order.OrderModel
import org.junit.Test
import spock.lang.Specification

import isv.cjl.payment.enums.PaymentTransactionType
import isv.sap.payment.model.IsvPaymentTransactionEntryModel
import isv.sap.payment.model.IsvPaymentTransactionModel
import isv.sap.payment.service.PaymentTransactionService

import static isv.cjl.payment.enums.AlternativePaymentMethod.KLI
import static isv.cjl.payment.enums.PaymentType.ALTERNATIVE_PAYMENT
import static java.util.Optional.of

@UnitTest
class KlarnaTakePaymentStrategySpec extends Specification
{
    def paymentTransactionService = Mock([useObjenesis: false], PaymentTransactionService)

    def strategy = new KlarnaTakePaymentStrategy()

    @Test
    def 'should create Klarna capture request'()
    {
        given:
        def order = new OrderModel()
        def transaction = new IsvPaymentTransactionModel(merchantId: 'mid')
        def transactionEntry = Mock([useObjenesis: false], IsvPaymentTransactionEntryModel)
        strategy.paymentTransactionService = paymentTransactionService
        paymentTransactionService.getLatestAcceptedTransactionEntry(transaction, de.hybris.platform.payment.enums.PaymentTransactionType.AUTHORIZATION) >> of(transactionEntry)

        when:
        def capture = strategy.request(order, transaction)
        def params = capture.requestParams

        then:
        capture.paymentTransactionType == PaymentTransactionType.CAPTURE

        params.merchantId == transaction.merchantId
        params.order == order
        params.transaction == transactionEntry
    }

    @Test
    def 'should return alternative payment type'()
    {
        expect:
        new KlarnaTakePaymentStrategy().paymentType == ALTERNATIVE_PAYMENT
    }

    @Test
    def 'should return Klarna alternative payment method'()
    {
        expect:
        new KlarnaTakePaymentStrategy().paymentMethod == KLI
    }
}
