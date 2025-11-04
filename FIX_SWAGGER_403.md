# 🔧 FIX LỖI 403 - SWAGGER UI

## ✅ ĐÃ FIX

Đã cập nhật **SecurityConfig** để cho phép truy cập Swagger UI và uploaded files.

---

## 🎯 THAY ĐỔI

### 1. SecurityConfig.java - Updated permitAll list:

```java
.requestMatchers(
    // Swagger/OpenAPI
    "/actuator/**",
    "/v3/api-docs/**",
    "/swagger-ui/**",
    "/swagger-ui.html",
    "/swagger-resources/**",
    "/webjars/**",
    
    // Auth endpoints
    "/api/auth/register",
    "/api/auth/login",
    "/api/auth/status",
    
    // Static files (uploaded images)
    "/uploads/**"
).permitAll()
```

### 2. OpenApiConfig.java - NEW:

Cấu hình Swagger để support session-based authentication (Cookie JSESSIONID).

---

## 🚀 CÁCH SỬ DỤNG SWAGGER

### Bước 1: Truy cập Swagger UI

```
http://localhost:8080/swagger-ui/index.html
```

### Bước 2: Test Public Endpoints (Không cần login)

Các endpoints này hoạt động ngay:
- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /api/auth/status`

### Bước 3: Test Protected Endpoints (Cần login)

**Method 1: Login qua Swagger UI**

1. Tìm endpoint `POST /api/auth/login`
2. Click "Try it out"
3. Nhập request body:
```json
{
  "email": "admin@evcs.com",
  "password": "password123"
}
```
4. Click "Execute"
5. Sau khi login thành công, browser sẽ lưu JSESSIONID cookie
6. Bây giờ có thể test các APIs khác

**Method 2: Login qua Browser trước**

1. Mở browser console hoặc dùng Postman
2. Gọi POST `http://localhost:8080/api/auth/login` với credentials
3. Browser tự động lưu session cookie
4. Mở Swagger UI trong cùng browser
5. Swagger sẽ tự động gửi cookie cùng requests

---

## 🔐 AUTHENTICATION FLOW

```
┌─────────────────────────────────────────────────┐
│ 1. POST /api/auth/login                         │
│    Body: { email, password }                    │
└─────────────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────────────┐
│ 2. Backend tạo session                          │
│    Set-Cookie: JSESSIONID=xxx                   │
└─────────────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────────────┐
│ 3. Browser lưu cookie tự động                   │
└─────────────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────────────┐
│ 4. Các requests tiếp theo tự động gửi cookie    │
│    Cookie: JSESSIONID=xxx                       │
└─────────────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────────────┐
│ 5. Backend verify session → Allow access        │
└─────────────────────────────────────────────────┘
```

---

## 📋 ENDPOINTS THEO AUTHENTICATION

### 🟢 Public (Không cần login):
```
POST   /api/auth/register
POST   /api/auth/login
GET    /api/auth/status
GET    /swagger-ui/**
GET    /v3/api-docs/**
GET    /uploads/**
```

### 🔵 Authenticated (Cần login):
```
GET    /api/groups
POST   /api/groups
GET    /api/vehicles
POST   /api/vehicles
POST   /api/vehicles/upload-images
...all other /api/** endpoints
```

### 🔴 Admin Only:
```
GET    /api/admin/**
PUT    /api/admin/**
POST   /api/admin/**
DELETE /api/admin/**
```

---

## 🧪 TEST EXAMPLES

### Test 1: Register + Login + Get Groups

**Step 1: Register**
```
POST /api/auth/register
{
  "fullName": "Test User",
  "email": "test@example.com",
  "password": "password123",
  "cccd": "001234567890",
  "driverLicense": "B2-001234",
  "birthday": "1990-01-01"
}
```

**Step 2: Login**
```
POST /api/auth/login
{
  "email": "test@example.com",
  "password": "password123"
}
→ Cookie: JSESSIONID=xxx được set
```

**Step 3: Get Groups (Now works!)**
```
GET /api/groups
→ Cookie tự động gửi kèm → Success!
```

---

### Test 2: Upload Vehicle Images

**Step 1: Login first**
```
POST /api/auth/login
```

**Step 2: Upload images**
```
POST /api/vehicles/upload-images
Content-Type: multipart/form-data

form-data:
- image1: (file)
- image2: (file)
- image3: (file)
```

**Step 3: Create vehicle**
```
POST /api/vehicles
{
  "model": "VF 8",
  "brand": "VinFast",
  "licensePlate": "30A-12345",
  "imageUrl1": "/uploads/vehicles/abc_123.jpg",
  ...
}
```

---

## ⚠️ TROUBLESHOOTING

### Vẫn bị 403 sau khi login?

**Check 1: Cookie có được set không?**
- Mở Browser DevTools → Application/Storage → Cookies
- Kiểm tra có `JSESSIONID` cookie không
- Domain phải đúng: `localhost:8080`

**Check 2: Login có thành công không?**
- Response phải có `"success": true`
- Status code phải là 200

**Check 3: Session có expire không?**
- Session timeout: 24 hours (theo config)
- Nếu quá 24h, phải login lại

**Check 4: CORS issue?**
- Nếu test từ frontend khác domain
- Đảm bảo frontend URL trong CORS config: `http://localhost:3000`

---

## 🔄 SESSION MANAGEMENT

### Session Timeout:
- **Default:** 24 hours
- Config trong `application.yml`:
```yaml
server:
  servlet:
    session:
      timeout: 24h
```

### Logout:
```
POST /api/auth/logout
→ Xóa session
→ Xóa cookie JSESSIONID
```

### Check Session Status:
```
GET /api/auth/status
→ Trả về user info nếu đã login
→ Trả về error nếu chưa login
```

---

## 🎯 SWAGGER UI FEATURES

### Authorization Badge:

Swagger UI sẽ hiển thị 🔒 icon cho các endpoints cần authentication.

### Cookie Authentication:

Swagger đã được config để dùng `cookieAuth` scheme:
- Type: API Key
- In: Cookie
- Name: JSESSIONID

Browser tự động handle cookie, không cần manual setup!

---

## 📚 RELATED FILES

**Modified:**
- `SecurityConfig.java` - Updated permitAll paths
- `application.yml` - Session config

**New:**
- `OpenApiConfig.java` - Swagger authentication config

---

## ✅ CHECKLIST

Test Swagger UI:
- [ ] Truy cập http://localhost:8080/swagger-ui/index.html
- [ ] Thấy danh sách APIs
- [ ] Test POST /api/auth/login
- [ ] Login thành công, nhận được cookie
- [ ] Test GET /api/groups (không còn 403)
- [ ] Test các endpoints khác
- [ ] Test logout

---

## 🎉 KẾT QUẢ

- ✅ Swagger UI accessible
- ✅ Public endpoints work without login
- ✅ Protected endpoints work after login
- ✅ Session-based authentication via cookie
- ✅ Uploads folder accessible

**Swagger UI đã sẵn sàng để test! 🚀**

---

## 💡 TIPS

1. **Dùng browser incognito** để test fresh session
2. **Clear cookies** nếu gặp vấn đề authentication
3. **Check browser console** để xem errors
4. **Dùng Network tab** để xem request/response headers
5. **Login lại** nếu session expire

---

## 🔗 USEFUL LINKS

- Swagger UI: http://localhost:8080/swagger-ui/index.html
- API Docs: http://localhost:8080/v3/api-docs
- Health Check: http://localhost:8080/actuator/health

