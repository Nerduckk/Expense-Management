# Hệ Thống Quản Lý Chi Tiêu Cá Nhân - Java Desktop Application

## Giới Thiệu
**Hệ Thống Quản Lý Chi Tiêu (Expense Manager)** là một ứng dụng desktop được phát triển bằng Java Core thuần túy, không sử dụng bất kỳ framework bên ngoài nào. Ứng dụng được xây dựng nhằm giải quyết bài toán quản lý tài chính cá nhân, giúp người dùng theo dõi dòng tiền ra vào, kiểm soát nợ nần và tự động hóa các khoản chi tiêu định kỳ.

Đây là giải pháp thay thế cho việc ghi chép sổ sách thủ công hoặc Excel, giúp giảm thiểu sai sót tính toán và nâng cao hiệu quả quản lý tài chính. Ứng dụng hỗ trợ các tính năng như: Quản lý nhiều ví/tài khoản, Phân loại thu chi, Theo dõi vay nợ chi tiết, và Tự động sinh giao dịch khi đến hạn thanh toán hóa đơn. Dữ liệu được lưu trữ an toàn dưới dạng file nhị phân (.dat).

## Mục Tiêu Dự Án
* **Tự động hóa:** Thay thế việc ghi chép tay bằng hệ thống số hóa.
* **Chính xác:** Đảm bảo tính toán số dư, tổng thu chi và dư nợ chính xác tuyệt đối.
* **Tối ưu hóa:** Quản lý dòng tiền và nợ nần minh bạch, rõ ràng.
* **Lưu trữ:** Dữ liệu được lưu trữ bền vững, không mất khi tắt ứng dụng.

## Chức Năng Hệ Thống Chi Tiết

### 1. Quản Lý Tài Khoản & Người Dùng (Class: User, Account)
* **Chức năng:**
    * **Quản lý người dùng:** Lưu trữ thông tin cơ bản (Tên, Email, Tiền tệ mặc định).
    * **Quản lý ví:** Tạo nhiều tài khoản (Ví tiền mặt, Ngân hàng, Tiết kiệm...).
    * **Cập nhật số dư:** Tự động cộng/trừ tiền khi có giao dịch phát sinh.
    * **Validate:** Kiểm tra số tiền không được âm, định dạng tiền tệ hợp lệ.

### 2. Quản Lý Giao Dịch Thu/Chi (Class: NormalTransaction, TransferTransaction)
* **Giao dịch thường:**
    * Ghi chép các khoản Thu (Lương, Thưởng...) và Chi (Ăn uống, Mua sắm...).
    * Phân loại theo Danh mục (`Category`) rõ ràng.
* **Chuyển khoản nội bộ:**
    * Chuyển tiền giữa các tài khoản của người dùng (Ví -> Ngân hàng).
    * Hỗ trợ tính phí chuyển khoản (nếu có).
    * Đảm bảo tính toàn vẹn: Trừ bên này, cộng bên kia.

### 3. Quản Lý Vay & Cho Vay (Class: Debt, DebtTransaction)
* **Quy trình Cho Vay (Lending):**
    * Tạo hồ sơ nợ: Lưu tên người vay, số tiền gốc.
    * Xuất tiền: Trừ tiền từ tài khoản người dùng, ghi nhận trạng thái "Đang nợ".
* **Quy trình Thu Nợ / Trả Nợ (Repayment):**
    * Ghi nhận giao dịch trả tiền.
    * Tự động cập nhật số tiền còn lại (`Remaining Amount`).
    * Cập nhật trạng thái nợ (Hoàn thành/Nợ xấu).

### 4. Hệ Thống Tự Động Hóa (Class: RecurringSchedule)
* **Lập lịch:** Cài đặt các khoản chi tiêu lặp lại (Tiền nhà, Internet, Netflix...).
* **Sinh giao dịch:**
    * Hệ thống tự động kiểm tra ngày hiện tại.
    * Tự động tạo giao dịch (`generateTxn`) và trừ tiền khi đến hạn.
    * Cập nhật số kỳ đã thanh toán.

### 5. Lưu Trữ Dữ Liệu (File I/O)
* Sử dụng cơ chế **Java Serialization** để lưu toàn bộ dữ liệu (Objects) xuống file `.dat`.
* Tự động tải lại dữ liệu cũ khi khởi động ứng dụng.

## Danh Sách Object (Entities) Chính
1.  **User:** Người dùng hệ thống.
2.  **Account:** Tài khoản tiền (Ví, ATM).
3.  **Category:** Hạng mục thu chi (Ăn uống, Lương...).
4.  **Transaction (Abstract):** Lớp cha cho mọi giao dịch.
    * *NormalTransaction:* Thu chi thường.
    * *TransferTransaction:* Chuyển khoản.
    * *DebtTransaction:* Giao dịch liên quan đến nợ.
5.  **Debt:** Hồ sơ khoản nợ.
6.  **RecurringSchedule:** Lịch thanh toán định kỳ.

---

## 👥 Danh Sách Thành Viên Nhóm

**Nhóm Bài Tập Lớn OOP**

| STT | Họ và Tên | Mã Sinh Viên | Vai Trò | Nhiệm vụ |
|:---:|:---|:---:|:---|:---|
| 1 | **Nguyễn Tiến Đạt** | B23DCDT059 | Thành viên | AbtractTransaction, RecurringSchedule, NormalTransaction, CycleType |
| 2 | **Dương Quốc Hoàng** | B23DCVT168 | Thành viên | ... |
| 3 | **Đỗ Minh Tiến** | B23DCDT254 | Thành viên | ... |
| 4 | **Lê Anh Việt** | B23DCVT453 | Trưởng nhóm | Xử lý DAO, Thiết kế giao diện chuẩn CRUD, Thiết kế mô hình dữ liệu, Xử lý Service|
| 5 | **Nguyễn Hoàng Anh Đức** | B23DCVT097 | Thành viên | ... |
