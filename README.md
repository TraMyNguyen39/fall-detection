
# Fall Detection App

### Giới thiệu
Fall Detection là một ứng dụng theo dõi té ngã tích hợp hoàn hảo với các thiết bị đeo sử dụng cảm biến MPU.

### Tính năng
* Đăng nhập và Đăng ký, Quản lý thông tin cá nhân.
* Cảnh báo phát hiện té ngã: Thông báo ngay cho người chăm sóc hoặc thành viên gia đình nếu phát hiện cú ngã.
* Đăng ký mang thiết bị: Gửi yêu cầu đăng ký thiết bị đeo được trang bị cảm biến MPU đến quản trị viên.
* Theo dõi bệnh nhân: Người chăm sóc gửi yêu cầu để theo dõi bệnh nhân để đảm bảo an toàn thông tin.

### Cài đặt
#### 1. Clone dự án từ GitHub (nhánh feat/final)
`git clone -b feat/final https://github.com/TraMyNguyen39/fall-detection.git`
#### 2. Thêm file _google-services.json_ được đính kèm vào thư mục _app/google-services.json_
#### 3. Mở và khởi chạy dự án bằng Android Studio
#### 4. Đăng nhập vào tài khoản người giám sát để _nhận thông báo_ khi có té ngã (bất kể đang ở trong hay ngoài ứng dụng)
    Demo Account: tram8319@gmail.com, password: abc123456

### Cấu trúc dự án
#### 1. app/src/main/java/com/example/falldetection/data/
* model/: Chứa các lớp mô hình dữ liệu.
* datasource/: Chứa các lớp liên quan đến dữ liệu từ xa.
* repository/: Chứa các lớp xử lý dữ liệu (repository). Repository sẽ tương tác với các nguồn dữ liệu (local và remote) và cung cấp dữ liệu cho ViewModel.

#### 2. app/src/main/java/com/example/falldetection/ui/
Chứa các acitivity và các thư mục con chứa tệp fragment, viewmodel, adapter tương ứng.
Trong đó:
* activity: Chứa các Activity của ứng dụng.
* fragment: Chứa các Fragment của ứng dụng.
* adapter: Chứa các adapter cho RecyclerView.
* viewmodel: Chứa các lớp ViewModel, nơi chứa logic xử lý và tương tác với repository để lấy dữ liệu.

#### 3. app/src/main/java/com/example/falldetection/utils/
Chứa các lớp tiện ích, hàm hỗ trợ dùng chung cho toàn bộ dự án.

#### 4. app/src/main/java/com/example/falldetection/MessageService.kt
Cấu hình thông báo được nhận từ Firebase trong tệp này.

#### 5. app/src/main/res/
* layout/: Chứa các tệp layout XML.
* values/: Chứa các tệp giá trị như strings, colors, styles, v.v.
* drawable/: Chứa các tài nguyên đồ họa như hình ảnh, vector, v.v.

#### 6. app/src/main/AndroidManifest.xml
Tệp cấu hình ứng dụng, khai báo các Activity, permission, v.v.
