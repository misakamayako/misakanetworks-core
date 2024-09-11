CREATE DATABASE IF NOT EXISTS MisakaNetworks DEFAULT CHARACTER SET utf8mb4;
USE MisakaNetworks;

CREATE TABLE IF NOT EXISTS article
(
    id         INT AUTO_INCREMENT PRIMARY KEY,
    title      VARCHAR(80) UNIQUE NOT NULL,
    folder     VARCHAR(40)        NOT NULL COMMENT '文章文件夹名称，随机生成',
    brief      TEXT,
    views      INT        DEFAULT 0,
    author     VARCHAR(100)       NOT NULL,
    has_delete TINYINT(1) DEFAULT 0,
    created_at TIMESTAMP  DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP  DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_title (title),
    INDEX idx_hasDelete (has_delete)
) COMMENT = '文章表'
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS article_history
(
    id         INT AUTO_INCREMENT PRIMARY KEY,
    version    INT COMMENT '文章版本号',
    article    INT NOT NULL,
    created_at TIMESTAMP                               DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP                               DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    status     ENUM ('draft', 'published', 'archived') DEFAULT 'published',
    FOREIGN KEY (article) REFERENCES article (id)
) COMMENT = '文章历史记录'
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS category
(
    id          INT AUTO_INCREMENT PRIMARY KEY,
    description VARCHAR(80) UNIQUE               NOT NULL,
    type        ENUM ('article','image','album') NOT NULL COMMENT '标签类型'
) COMMENT = '标签数据'
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS article_to_category
(
    id       INT AUTO_INCREMENT PRIMARY KEY,
    article  INT,
    category INT,
    FOREIGN KEY (article) REFERENCES article (id),
    FOREIGN KEY (category) REFERENCES category (id)
);

CREATE TABLE IF NOT EXISTS users
(
    id       INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50)  NOT NULL UNIQUE,
    password VARCHAR(500) NOT NULL,
    enabled  BOOLEAN      NOT NULL,
    UNIQUE INDEX ix_auth_username (username)
);

CREATE TABLE IF NOT EXISTS authorities
(
    userId    INT         NOT NULL,
    authority VARCHAR(50) NOT NULL,
    CONSTRAINT fk_authorities_users FOREIGN KEY (userId) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS file_mapping_of_article
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    bucket       VARCHAR(31)  NOT NULL COMMENT 'oss bucket name, don\'t store temp bucket',
    file_key     VARCHAR(255) NOT NULL UNIQUE,
    connect_file INT,
    delete_flag  TINYINT(1) DEFAULT 0,
    CONSTRAINT fk_file_to_article FOREIGN KEY (connect_file) REFERENCES article (id),
    INDEX (connect_file)
);

CREATE TABLE IF NOT EXISTS images
(
    id          INT AUTO_INCREMENT PRIMARY KEY,
    oss_key     VARCHAR(255) NOT NULL COMMENT '文件的oss key',
    title       VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    upload_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    has_delete  TINYINT               DEFAULT 0 COMMENT '是否已删除'
) COMMENT = '非在文章中使用的图片数据'
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS albums
(
    id                        INT AUTO_INCREMENT PRIMARY KEY,
    name                      VARCHAR(255) NOT NULL UNIQUE COMMENT '相册名称',
    description               TEXT COMMENT '描述',
    cover_id                  INT       DEFAULT NULL COMMENT '相册图片id',
    create_at                 TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_at                 TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_private                TINYINT   DEFAULT 0 COMMENT '相册是否私有，图片继承这个属性',
    contains_explicit_content TINYINT   DEFAULT 0,
    has_delete                TINYINT   DEFAULT 0 COMMENT '是否已删除',
    CONSTRAINT fk_cover_id FOREIGN KEY (cover_id) REFERENCES images (id) ON DELETE SET NULL
) COMMENT = '相册数据'
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS albums_to_category
(
    album_id    INT NOT NULL,
    category_id INT NOT NULL,
    CONSTRAINT fk_album_id FOREIGN KEY (album_id) REFERENCES albums (id) ON DELETE CASCADE,
    CONSTRAINT fk_category_id FOREIGN KEY (category_id) REFERENCES category (id) ON DELETE CASCADE
) COMMENT = '相册和类型的联系'
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;
