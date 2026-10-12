# =====================================================================
#  EcoGate (SkyGateHub) - Scaffolding script (PowerShell)
#  Chay tai thu muc goc project (noi co pom.xml):
#      powershell -ExecutionPolicy Bypass -File .\scaffold.ps1
#  - Chi tao thu muc + file RONG.
#  - KHONG ghi de file da ton tai (application.properties, Application...).
# =====================================================================
$ErrorActionPreference = 'Stop'
if (-not (Test-Path '.\pom.xml')) { throw 'Hay chay script tai thu muc goc project (co pom.xml).' }

$J  = 'src/main/java/com/example/skygatehub'
$T  = 'src/test/java/com/example/skygatehub'
$R  = 'src/main/resources'
$TP = "$R/templates"
$ST = "$R/static"

# Ham sinh entity/dto/... nhanh: (thu muc goc, thu muc con, danh sach ten class)
function J([string]$pkg, [string[]]$classes) { $classes | ForEach-Object { "$J/$pkg/$_.java" } }
# Service interface + impl
function S([string]$feature, [string[]]$names) {
    foreach ($n in $names) { "$J/$feature/service/$n.java"; "$J/$feature/service/impl/${n}Impl.java" }
}

$files = @(
    # ======================= [C] COMMON =======================
    (J 'common/config'    @('WebMvcConfig','AppProperties'))
    (J 'common/constant'  @('AppConstants','SessionKeys'))
    (J 'common/enums'     @('RoleName','LoaiKhach','LoaiNgay'))
    (J 'common/exception' @('ErrorCode','BusinessException','ResourceNotFoundException','GlobalExceptionHandler'))
    (J 'common/security'  @('CurrentUser','RequireRole','AuthInterceptor','PasswordHasher'))
    (J 'common/util'      @('DayTypeResolver','DateRangeUtils','MoneyUtils','OrderCodeGenerator'))
    (J 'common/web'       @('HomeController','GlobalModelAdvice'))
    "$R/application-dev.properties"
    "$R/messages.properties"
    "$R/db/mysql/01_schema.sql"
    "$R/db/mysql/02_seed_data.sql"
    "$TP/layout/customer.html", "$TP/layout/admin.html"
    "$TP/fragments/header.html", "$TP/fragments/footer.html", "$TP/fragments/admin-sidebar.html"
    "$TP/fragments/flash-messages.html", "$TP/fragments/pagination.html"
    "$TP/index.html"
    "$TP/error/403.html", "$TP/error/404.html", "$TP/error/500.html"
    "$ST/css/base.css", "$ST/css/customer.css", "$ST/css/admin.css"
    "$ST/js/common.js"
    "$ST/images/tickets/.gitkeep", "$ST/images/rooms/.gitkeep", "$ST/images/dining/.gitkeep"

    # ======================= [M1] AUTH =======================
    (J 'auth/dto'        @('LoginRequest','RegisterRequest'))
    (S 'auth'            @('AuthService'))
    (J 'auth/controller' @('AuthController'))

    # ======================= [M1] ACCOUNT =======================
    (J 'account/entity'     @('NguoiDung','VaiTro'))
    (J 'account/enums'      @('TrangThaiNguoiDung'))
    (J 'account/repository' @('NguoiDungRepository','VaiTroRepository'))
    (J 'account/dto'        @('UserCreateRequest','ProfileUpdateRequest','UserResponse'))
    (S 'account'            @('AccountService'))
    (J 'account/controller' @('ProfileController','AdminAccountController'))
    (J 'account/config'     @('AccountDataSeeder'))

    # ======================= [M1] TICKET =======================
    (J 'ticket/entity'     @('SanPhamVe','ComboChiTiet','ComboChiTietId','BangGiaVe','DatVe'))
    (J 'ticket/enums'      @('TrangThaiSanPham'))
    (J 'ticket/repository' @('SanPhamVeRepository','ComboChiTietRepository','BangGiaVeRepository','DatVeRepository'))
    (J 'ticket/dto'        @('SanPhamVeRequest','ComboItemRequest','BangGiaVeRequest','SanPhamVeResponse','TicketQuoteRequest','TicketQuoteResponse'))
    (S 'ticket'            @('SanPhamVeService','BangGiaVeService','TicketPricingService','TicketBookingService'))
    (J 'ticket/controller' @('TicketController','AdminTicketController','AdminTicketPriceController'))
    "$TP/auth/login.html", "$TP/auth/register.html"
    "$TP/account/customer/profile.html"
    "$TP/account/admin/list.html", "$TP/account/admin/form.html"
    "$TP/ticket/customer/list.html", "$TP/ticket/customer/detail.html"
    "$TP/ticket/admin/list.html", "$TP/ticket/admin/form.html", "$TP/ticket/admin/combo-form.html", "$TP/ticket/admin/price-list.html"
    "$ST/css/modules/ticket.css"
    "$ST/js/modules/ticket-quote.js", "$ST/js/modules/auth.js"

    # ======================= [M2] ROOMBOOKING =======================
    (J 'roombooking/entity'     @('DatPhong'))
    (J 'roombooking/enums'      @('TrangThaiDatPhong'))
    (J 'roombooking/repository' @('DatPhongRepository'))
    (J 'roombooking/dto'        @('RoomSearchRequest','RoomAvailabilityResponse','RoomQuoteRequest','RoomQuoteResponse','NightPriceLine'))
    (S 'roombooking'            @('RoomAvailabilityService','RoomPricingService','RoomBookingService'))
    (J 'roombooking/controller' @('RoomBookingController','RoomQuoteApiController'))
    "$TP/roombooking/customer/search.html", "$TP/roombooking/customer/results.html", "$TP/roombooking/customer/booking-form.html"
    "$ST/css/modules/room-booking.css"
    "$ST/js/modules/room-search.js"

    # ======================= [M3] ROOM (ADMIN) =======================
    (J 'room/entity'     @('LoaiPhong','Phong','PhanPhong'))
    (J 'room/enums'      @('TrangThaiPhong'))
    (J 'room/repository' @('LoaiPhongRepository','PhongRepository','PhanPhongRepository'))
    (J 'room/dto'        @('LoaiPhongRequest','PhongRequest','RoomAssignRequest','PendingAssignmentResponse','RoomMapCell'))
    (S 'room'            @('LoaiPhongService','PhongService','RoomAssignmentService','RoomMapService'))
    (J 'room/controller' @('AdminRoomTypeController','AdminRoomController','AdminRoomMapController','AdminRoomAssignmentController'))
    "$TP/room/admin/type-list.html", "$TP/room/admin/type-form.html"
    "$TP/room/admin/room-list.html", "$TP/room/admin/room-form.html", "$TP/room/admin/room-map.html"
    "$TP/room/admin/assignment-list.html", "$TP/room/admin/assignment-form.html"
    "$ST/css/modules/room-admin.css"
    "$ST/js/modules/room-map.js"

    # ======================= [M4] DINING =======================
    (J 'dining/entity'     @('DichVuAn','BanAn','DatAn'))
    (J 'dining/enums'      @('LoaiDichVuAn','CaAn','TrangThaiBan'))
    (J 'dining/repository' @('DichVuAnRepository','BanAnRepository','DatAnRepository'))
    (J 'dining/dto'        @('DichVuAnRequest','BanAnRequest','MealBookingRequest','TableAssignRequest','MealScheduleRow'))
    (S 'dining'            @('DichVuAnService','BanAnService','MealBookingService','TableAssignmentService'))
    (J 'dining/controller' @('DiningController','AdminDiningServiceController','AdminTableController','AdminTableAssignmentController'))
    "$TP/dining/customer/menu.html", "$TP/dining/customer/booking-form.html"
    "$TP/dining/admin/service-list.html", "$TP/dining/admin/service-form.html"
    "$TP/dining/admin/table-list.html", "$TP/dining/admin/table-form.html"
    "$TP/dining/admin/meal-schedule.html", "$TP/dining/admin/table-assignment.html"
    "$ST/css/modules/dining.css"
    "$ST/js/modules/dining.js"

    # ======================= [M5] CART =======================
    (J 'cart/model'      @('Cart','CartItem'))
    (J 'cart/enums'      @('CartItemType'))
    (S 'cart'            @('CartService'))
    (J 'cart/controller' @('CartController'))

    # ======================= [M5] ORDER =======================
    (J 'order/entity'     @('DonDat'))
    (J 'order/enums'      @('TrangThaiDon'))
    (J 'order/repository' @('DonDatRepository'))
    (J 'order/dto'        @('CheckoutRequest','OrderSummaryResponse','OrderDetailResponse'))
    (S 'order'            @('CheckoutService','OrderService'))
    (J 'order/service'    @('OrderTotalCalculator'))
    (J 'order/controller' @('CheckoutController','OrderController','AdminOrderController'))

    # ======================= [M5] PAYMENT =======================
    (J 'payment/entity'     @('ThanhToan'))
    (J 'payment/enums'      @('LoaiThanhToan','PhuongThucThanhToan','TrangThaiThanhToan'))
    (J 'payment/repository' @('ThanhToanRepository'))
    (J 'payment/dto'        @('PaymentRequest','PaymentResponse'))
    (S 'payment'            @('PaymentService'))
    (J 'payment/service'    @('MockPaymentGateway'))
    (J 'payment/controller' @('PaymentController','AdminPaymentController'))

    # ======================= [M5] CHECKIN =======================
    (J 'checkin/entity'     @('LichSuCheckIn'))
    (J 'checkin/enums'      @('LoaiDichVuCheckIn'))
    (J 'checkin/repository' @('LichSuCheckInRepository'))
    (J 'checkin/dto'        @('CheckInLookupResponse','CheckInRequest'))
    (S 'checkin'            @('CheckInService'))
    (J 'checkin/controller' @('AdminCheckInController'))

    # ======================= [M5] DASHBOARD =======================
    (J 'dashboard/dto'        @('DashboardStats','RevenuePoint'))
    (J 'dashboard/repository' @('DashboardQueryRepository'))
    (S 'dashboard'            @('DashboardService'))
    (J 'dashboard/controller' @('AdminDashboardController'))

    # ======================= [M5] VIEWS =======================
    "$TP/cart/customer/view.html"
    "$TP/order/customer/checkout.html", "$TP/order/customer/my-orders.html", "$TP/order/customer/detail.html"
    "$TP/order/admin/list.html", "$TP/order/admin/detail.html"
    "$TP/payment/customer/pay.html", "$TP/payment/customer/result.html"
    "$TP/payment/admin/list.html", "$TP/payment/admin/collect.html"
    "$TP/checkin/admin/lookup.html", "$TP/checkin/admin/detail.html"
    "$TP/dashboard/admin/index.html"
    "$ST/css/modules/cart-order.css"
    "$ST/js/modules/cart.js", "$ST/js/modules/checkin.js", "$ST/js/modules/dashboard.js"

    # ======================= UNIT TESTS =======================
    "$T/common/util/DayTypeResolverTest.java"
    "$T/ticket/service/TicketPricingServiceTest.java"
    "$T/roombooking/service/RoomAvailabilityServiceTest.java"
    "$T/roombooking/service/RoomPricingServiceTest.java"
    "$T/room/service/RoomAssignmentServiceTest.java"
    "$T/dining/service/TableAssignmentServiceTest.java"
    "$T/order/service/OrderTotalCalculatorTest.java"
    "$T/payment/service/PaymentServiceTest.java"
    "$T/checkin/service/CheckInServiceTest.java"
) | ForEach-Object { $_ }   # lam phang mang long nhau

$created = 0; $skipped = 0
foreach ($f in $files) {
    $dir = Split-Path $f -Parent
    if (-not (Test-Path $dir)) { New-Item -ItemType Directory -Path $dir -Force | Out-Null }
    if (Test-Path $f) { $skipped++; continue }
    New-Item -ItemType File -Path $f | Out-Null
    $created++
}
Write-Host ("Hoan tat: tao moi {0} file, bo qua {1} file da ton tai (tong {2})." -f $created, $skipped, $files.Count) -ForegroundColor Green
