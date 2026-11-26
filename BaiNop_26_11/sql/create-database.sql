-- Chạy bằng SQL Server Management Studio, chỉ cần tạo database.
-- Các bảng categories / videos sẽ được Hibernate tự tạo (hibernate.hbm2ddl.auto=update)
-- khi chạy ứng dụng hoặc chạy vn.iotstar.config.Test.
IF DB_ID('ShoppingJPA') IS NULL
    CREATE DATABASE ShoppingJPA;
GO

-- Dự phòng: nếu Hibernate không tự tạo được bảng, chạy thủ công đoạn dưới đây.
USE ShoppingJPA;
GO
IF OBJECT_ID('categories', 'U') IS NULL
    CREATE TABLE categories (
        categoryId   INT IDENTITY(1,1) PRIMARY KEY,
        categoryname NVARCHAR(255) NOT NULL,
        images       NVARCHAR(255) NULL,
        status       INT NOT NULL DEFAULT 1
    );
GO
IF OBJECT_ID('videos', 'U') IS NULL
    CREATE TABLE videos (
        videoId     NVARCHAR(255) PRIMARY KEY,
        active      BIT NOT NULL DEFAULT 0,
        description NVARCHAR(MAX) NULL,
        poster      NVARCHAR(255) NULL,
        title       NVARCHAR(255) NULL,
        views       INT NOT NULL DEFAULT 0,
        videoFile   NVARCHAR(255) NULL,
        createdDate DATETIME2     NULL,
        categoryId  INT NULL REFERENCES categories(categoryId),
        userId      INT NULL      -- khoá ngoại tới users (quan hệ 1 - n), tạo ở cuối file
    );
GO
IF OBJECT_ID('users', 'U') IS NULL
    CREATE TABLE users (
        userId      INT IDENTITY(1,1) PRIMARY KEY,
        username    NVARCHAR(50)  NOT NULL UNIQUE,
        password    NVARCHAR(255) NOT NULL,
        fullname    NVARCHAR(100) NOT NULL,
        email       NVARCHAR(100) NOT NULL UNIQUE,
        createdDate DATETIME2     NULL,
        active      BIT NOT NULL DEFAULT 1,
        otp         NVARCHAR(10)  NULL,
        otpPurpose  NVARCHAR(20)  NULL,
        otpExpiry   DATETIME2     NULL,
        otpAttempts INT NOT NULL DEFAULT 0
    );
GO

-- Nâng cấp database đã có từ phiên bản trước (Hibernate hbm2ddl=update cũng tự làm việc này).
-- Tài khoản cũ được đặt active = 1 để vẫn đăng nhập được.
IF COL_LENGTH('users', 'active')      IS NULL ALTER TABLE users ADD active BIT NOT NULL DEFAULT 1;
IF COL_LENGTH('users', 'otp')         IS NULL ALTER TABLE users ADD otp NVARCHAR(10) NULL;
IF COL_LENGTH('users', 'otpPurpose')  IS NULL ALTER TABLE users ADD otpPurpose NVARCHAR(20) NULL;
IF COL_LENGTH('users', 'otpExpiry')   IS NULL ALTER TABLE users ADD otpExpiry DATETIME2 NULL;
IF COL_LENGTH('users', 'otpAttempts') IS NULL ALTER TABLE users ADD otpAttempts INT NOT NULL DEFAULT 0;
IF COL_LENGTH('videos', 'videoFile')   IS NULL ALTER TABLE videos ADD videoFile NVARCHAR(255) NULL;
IF COL_LENGTH('videos', 'createdDate') IS NULL ALTER TABLE videos ADD createdDate DATETIME2 NULL;
IF COL_LENGTH('videos', 'userId')      IS NULL ALTER TABLE videos ADD userId INT NULL;
GO
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = 'FK_videos_users')
    ALTER TABLE videos ADD CONSTRAINT FK_videos_users FOREIGN KEY (userId) REFERENCES users(userId);
GO
