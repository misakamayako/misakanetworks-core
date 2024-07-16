create database if not exists MisakaNetworks DEFAULT CHARACTER SET utf8mb4;
use MisakaNetworks;
CREATE TABLE article
(
    id           INT AUTO_INCREMENT PRIMARY KEY,
    title        VARCHAR(255) NOT NULL,
    markdown_url VARCHAR(512) NOT NULL,
    html_url     VARCHAR(512) NOT NULL,
    brief        VARCHAR(200) NOT NULL,
    author       VARCHAR(100) NOT NULL,
    hasDelete    tinyint(1) default 0,
    created_at   TIMESTAMP  DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP  DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
create table if not exists article_to_category
(
    id       int primary key auto_increment,
    article  int,
    category int,
    foreign key (article) references article (id),
    foreign key (category) references category (id)
);
create table if not exists category
(
    id          int primary key auto_increment,
    description varchar(80) unique not null,
    type        int                not null comment '标签类型，暂定：1 文章，2：图片'
);
create table if not exists file_mapping
(
    id          int auto_increment primary key,
    eigenvalues varchar(64) not null unique,
    real_name   varchar(126) default null,
    create_at   datetime    not null,
    delete_flag bool         default false
);
create table if not exists album
(
    id      int primary key auto_increment,
    title   varchar(20) not null,
    cover   varchar(120),
    grading int  default 1 comment '1：健全，2：16x,3:18x',
    private bool default false
);
create table if not exists album_to_category
(
    id          int primary key auto_increment,
    album_id    int not null,
    category_id int not null,
    foreign key (album_id) references album (id),
    foreign key (category_id) references category (id)
);
create table if not exists img
(
    id          int primary key auto_increment,
    eigenvalues varchar(64) not null unique,
    name        varchar(64) not null,
    grading     int  default 1 comment '1：健全，2：16x,3:18x',
    private     bool default false,
    album       int  default null,
    foreign key (album) references album (id)
);
create table if not exists img_to_category
(
    id          int primary key auto_increment,
    img_id      int not null,
    category_id int not null,
    foreign key (img_id) references img (id),
    foreign key (category_id) references category (id)
);
CREATE TABLE if not exists users
(
    id       int primary key auto_increment,
    username VARCHAR(50)  NOT NULL unique,
    password VARCHAR(500) NOT NULL,
    enabled  BOOLEAN      NOT NULL,
    UNIQUE INDEX ix_auth_username (username)
);
CREATE TABLE if not exists authorities
(
    userId    int         NOT NULL,
    authority VARCHAR(50) NOT NULL,
    CONSTRAINT fk_authorities_users FOREIGN KEY (userId) REFERENCES users (id)
);
create table if not exists file_mapping_of_article
(
    id           bigint auto_increment primary key,
    bucket       varchar(31)  not null comment 'oss bucket name, don\'t store temp bucket',
    file_key     varchar(255) not null unique,
    connect_file bigint,
    delete_flag  tinyint(1) default 0,
    CONSTRAINT fk_file_to_article foreign key (connect_file) REFERENCES article (id),
    index (connect_file)
);
