package com.avivba.simbanyu.api

import com.avivba.simbanyu.api.config.properties.DemoProperties
import com.avivba.simbanyu.api.config.properties.FileStorageProperties
import com.avivba.simbanyu.api.config.properties.JwtProperties
import com.avivba.simbanyu.api.features.bill.BillItemRepository
import com.avivba.simbanyu.api.features.bill.BillRepository
import com.avivba.simbanyu.api.features.bill.model.Bill
import com.avivba.simbanyu.api.features.bill.model.BillItem
import com.avivba.simbanyu.api.features.bill.model.BillStatus
import com.avivba.simbanyu.api.features.notification.NotificationRepository
import com.avivba.simbanyu.api.features.notification.model.Notification
import com.avivba.simbanyu.api.features.payment.PaymentRepository
import com.avivba.simbanyu.api.features.payment.PaymentService
import com.avivba.simbanyu.api.features.payment.model.Payment
import com.avivba.simbanyu.api.features.payment.model.PaymentStatus
import com.avivba.simbanyu.api.features.plans.PlanRepository
import com.avivba.simbanyu.api.features.plans.model.Plan
import com.avivba.simbanyu.api.features.prices.PriceRepository
import com.avivba.simbanyu.api.features.prices.model.Price
import com.avivba.simbanyu.api.features.subscriber.SubscriberRepository
import com.avivba.simbanyu.api.features.subscriber.SubscriberService
import com.avivba.simbanyu.api.features.subscriber.model.AddSubscriberRequest
import com.avivba.simbanyu.api.features.upload.UploadRepository
import com.avivba.simbanyu.api.features.upload.UploadService
import com.avivba.simbanyu.api.features.upload.model.Upload
import com.avivba.simbanyu.api.features.usage.UsageRepository
import com.avivba.simbanyu.api.features.usage.UsageSpecs
import com.avivba.simbanyu.api.features.usage.model.Usage
import com.avivba.simbanyu.api.features.user.UserRepository
import com.avivba.simbanyu.api.features.user.model.User
import com.avivba.simbanyu.api.features.user.model.UserRole
import jakarta.transaction.Transactional
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVRecord
import org.springframework.boot.CommandLineRunner
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.net.HttpURLConnection
import java.net.URI
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.Date
import kotlin.random.Random

@Component
class Seed(
    private val demoProperties: DemoProperties,
    private val jwtProperties: JwtProperties,
    private val storageProps: FileStorageProperties,
    private val userRepository: UserRepository,
    private val planRepository: PlanRepository,
    private val priceRepository: PriceRepository,
    private val subscriberService: SubscriberService,
    private val subscriberRepository: SubscriberRepository,
    private val uploadRepository: UploadRepository,
    private val usageRepository: UsageRepository,
    private val billRepository: BillRepository,
    private val billItemRepository: BillItemRepository,
    private val paymentRepository: PaymentRepository,
    private val paymentService: PaymentService,
    private val notificationRepository: NotificationRepository,
    private val encoder: PasswordEncoder
) : CommandLineRunner {

    @Transactional
    override fun run(vararg args: String) {
        if (userRepository.count() > 0) return
        val usersData = readCsv("seeds/users.csv") {
            User(
                name = it["name"]!!,
                username = it["username"]!!,
                password = encoder.encode(it["password"]!!)!!,
                phoneNumber = "0" + it["phone"]!!,
                role = UserRole.valueOf(it["role"]!!),
            )
        }.toMutableList()

        usersData += userRepository.save(
            User(
                name = "Seeder",
                username = "seeder0",
                password = encoder.encode(jwtProperties.key)!!,
                phoneNumber = "000000000",
                role = UserRole.ADMIN
            )
        )

        userRepository.saveAll(usersData)

        val seederUser = userRepository.findByUsername("seeder0")

        val plansData = readCsv("seeds/plans.csv") {
            Plan(
                name = it["name"],
                basePrice = it["basePrice"].toDouble(),
            )
        }

        planRepository.saveAll(plansData)

        val pricesData = readCsv("seeds/prices.csv") {
            val plan = planRepository.findPlanByName(it["plan"]!!).firstOrNull()
                ?: throw Exception("Plan not found")

            Price(
                name = it["name"],
                tierOrder = it["order"].toInt(),
                plan = plan,
                maxVolume = if (it["maxVolume"].isNullOrEmpty()) null else it["maxVolume"].toInt(),
                pricePerMeter = it["pricePerMeter"].toDouble(),
            )
        }

        priceRepository.saveAll(pricesData)

        val subscribersData = readCsv("seeds/subscribers.csv") {
            val coordinateValue = it["coordinate"]
            val coordinate = getCoordinate(coordinateValue)

            val plan = planRepository.findPlanByName(it["plan"]!!).firstOrNull()
                ?: throw Exception("Plan not found")

            AddSubscriberRequest(
                name = it["name"],
                phoneNumber = "0" + it["phone"],
                password = encoder.encode(it["password"])!!,
                meterId = it["meterId"],
                initialMeter = it["initialMeter"].toInt(),
                planId = plan.id.toString(),
                address = it["address"],
                longitude = coordinate.first,
                latitude = coordinate.second
            )
        }

        subscribersData.forEach {
            println("Adding subscriber: ${it.name}")
            subscriberService.addSubscriber(
                it,
                seederUser.username
            )
        }

        // DEMO SEED

        if (demoProperties.enabled) {
            subscriberRepository.findAll().forEach { subscriber ->
                val usageSpecs = UsageSpecs.filterBy(subscriber.id.toString(), includeInitial = true)
                var previousUsage = usageRepository.findAll(usageSpecs).first()

                var previousLocalDate = LocalDate
                    .of(2023, 1, 1)
                    .atTime(10, 5)

                val previousDate = previousLocalDate
                    .atZone(ZoneId.systemDefault())
                    .toInstant()

                previousUsage.createdAt = previousDate
                previousUsage.updatedAt = previousDate
                previousUsage = usageRepository.save(previousUsage)

                val today = LocalDateTime.now()
                val numMonths = ChronoUnit.MONTHS.between(previousLocalDate, today)

                for (i in 1..numMonths) {
                    // Usage
                    val dateLocal = previousLocalDate.plusMonths(1)
                    val date = dateLocal.atZone(ZoneId.systemDefault()).toInstant()
                    val currentUsageAmount = Random.nextInt(10, 20)
                    val usage = Usage(
                        meterStart = previousUsage.meterEnd,
                        meterEnd = currentUsageAmount,
                        evidenceImage = createDemoUpload(),
                        operatorNotes = "",
                        subscriber = subscriber,
                        createdAt = date,
                        updatedAt = date
                    )

                    previousUsage = usageRepository.save(usage)
                    previousLocalDate = dateLocal

                    // Bill
                    val bill = billRepository.save(
                        Bill(
                            amount = 1000.0,
                            status = if (numMonths - i < 4) BillStatus.UNPAID else BillStatus.PAID,
                            subscriber = subscriber,
                            usage = usage,
                            createdAt = date,
                            updatedAt = date,
                        )
                    )

                    val usageAmount = currentUsageAmount - previousUsage.meterEnd
                    mapOf(
                        "Biaya tetap" to 5000.0,
                        "Biaya penggunaan (${usageAmount}m³)" to usageAmount * 1500.0,
                        "Biaya admin" to 1000.0,
                    ).forEach { (name, amount) ->
                        billItemRepository.save(
                            BillItem(
                                name = name,
                                amount = amount,
                                bill = bill,
                                createdAt = date,
                                updatedAt = date
                            )
                        )
                    }

                    // Payment
                    if (bill.status == BillStatus.PAID) {
                        val paymentTypes = listOf("cash", "gopay", "shopeepay", "cstore", "bank_transfer", "qris")
                        paymentRepository.save(
                            Payment(
                                orderId = paymentService.createTransactionId(),
                                paymentType = paymentTypes.random(),
                                paymentUrl = "https://www.google.com/",
                                status = PaymentStatus.SUCCESS,
                                bill = bill,
                                createdAt = date,
                                updatedAt = date,
                            )
                        )
                    }

                    // Notification
                    val notifications = (1..15).map { i ->
                        Notification(
                            title = "Notification $i",
                            body = "Demo notification content $i",
                            user = subscriber.user,
                            createdAt = date,
                            updatedAt = date
                        )
                    }
                    notificationRepository.saveAll(notifications)
                }
            }
        }
    }

    private fun createDemoUpload(): Upload {
        val input = File("seeds/demo.png")
        val demoFile = uploadRepository.save(
            Upload(
                contentType = "image/png",
            )
        )
        val targetFile = File(storageProps.uploadDir, demoFile.id.toString())
        targetFile.parentFile.mkdirs()
        input.copyTo(targetFile)
        return demoFile
    }

    private fun getCoordinate(coordinateString: String): Pair<Double, Double> {
        val parts = coordinateString.split(",")
        val lat = parts[0].trim().toDouble()
        val lng = parts[1].trim().toDouble()

        return lat to lng
    }

    private fun <T : Any> readCsv(path: String, map: (record: CSVRecord) -> T?): List<T> {
        val file = File(path)
        if (file.exists()) {
            return CSVFormat.Builder.create(CSVFormat.DEFAULT).apply {
                setIgnoreSurroundingSpaces(true)
                setHeader()
                setSkipHeaderRecord(true)
            }.get().parse(file.inputStream().reader())
                .mapNotNull(map)
        } else {
            throw Exception("File $path not found")
        }
    }
}
