-- RDBMS Type   : PostgreSQL
-- Application  : A5:SQL Mk-2

-- 書籍
create table tbl_book (
  book_id bigserial not null
  , book_name varchar(255) not null
  , total_page integer not null
  , isbn varchar(20) not null
  , published_at date not null
  , author_id bigint not null
  , publisher_id bigint not null
  , genre_id bigint not null
  , valid_start_date date not null
  , valid_end_date date not null
  , created_by varchar(100) not null
  , created_at timestamp default CURRENT_TIMESTAMP  not null
  , updated_by varchar(100) not null
  , updated_at timestamp default CURRENT_TIMESTAMP  not null
  , constraint tbl_book_PKC primary key (book_id)
) ;

create unique index idx_uniq_isbn
  on tbl_book(isbn);

create index idx_author_id
  on tbl_book(author_id);

-- ジャンル
create table tbl_genre (
  genre_id bigserial not null
  , genre_name varchar(100) not null
  , sort_order integer not null
  , valid_start_date date not null
  , valid_end_date date not null
  , created_by varchar(100) not null
  , created_at timestamp default CURRENT_TIMESTAMP not null
  , updated_by varchar(100) not null
  , updated_at timestamp default CURRENT_TIMESTAMP not null
  , constraint tbl_genre_PKC primary key (genre_id)
) ;

-- 出版社
create table tbl_publisher (
  publisher_id bigserial not null
  , publisher_name varchar(100) not null
  , sort_order integer not null
  , valid_start_date date not null
  , valid_end_date date not null
  , created_by varchar(100) not null
  , created_at timestamp default CURRENT_TIMESTAMP not null
  , updated_by varchar(100) not null
  , updated_at timestamp default CURRENT_TIMESTAMP not null
  , constraint tbl_publisher_PKC primary key (publisher_id)
) ;

-- 著者
create table tbl_author (
  author_id bigserial not null
  , author_name varchar(255) not null
  , brief_history text not null
  , valid_start_date date not null
  , valid_end_date date not null
  , created_by varchar(100) not null
  , created_at timestamp default CURRENT_TIMESTAMP not null
  , updated_by varchar(100) not null
  , updated_at timestamp default CURRENT_TIMESTAMP not null
  , constraint tbl_author_PKC primary key (author_id)
) ;

alter table tbl_book
  add constraint tbl_book_FK1 foreign key (publisher_id) references tbl_publisher(publisher_id);

alter table tbl_book
  add constraint tbl_book_FK2 foreign key (genre_id) references tbl_genre(genre_id);

alter table tbl_book
  add constraint tbl_book_FK3 foreign key (author_id) references tbl_author(author_id);

comment on table tbl_book is '書籍';
comment on column tbl_book.book_id is '書籍ID';
comment on column tbl_book.book_name is '書籍名';
comment on column tbl_book.total_page is '総ページ数';
comment on column tbl_book.isbn is 'ISBN';
comment on column tbl_book.published_at is '出版日';
comment on column tbl_book.author_id is '著者ID';
comment on column tbl_book.publisher_id is '出版社ID';
comment on column tbl_book.genre_id is 'ジャンルID';
comment on column tbl_book.valid_start_date is '有効開始日';
comment on column tbl_book.valid_end_date is '有効終了日';
comment on column tbl_book.created_by is '作成者';
comment on column tbl_book.created_at is '作成日時';
comment on column tbl_book.updated_by is '更新者';
comment on column tbl_book.updated_at is '更新日時';

comment on table tbl_genre is 'ジャンル';
comment on column tbl_genre.genre_id is 'ジャンルID';
comment on column tbl_genre.genre_name is 'ジャンル名';
comment on column tbl_genre.sort_order is 'ソート順';
comment on column tbl_genre.valid_start_date is '有効開始日';
comment on column tbl_genre.valid_end_date is '有効終了日';
comment on column tbl_genre.created_by is '作成者';
comment on column tbl_genre.created_at is '作成日時';
comment on column tbl_genre.updated_by is '更新者';
comment on column tbl_genre.updated_at is '更新日時';

comment on table tbl_publisher is '出版社';
comment on column tbl_publisher.publisher_id is '出版社ID';
comment on column tbl_publisher.publisher_name is '出版社名';
comment on column tbl_publisher.sort_order is 'ソート順';
comment on column tbl_publisher.valid_start_date is '有効開始日';
comment on column tbl_publisher.valid_end_date is '有効終了日';
comment on column tbl_publisher.created_by is '作成者';
comment on column tbl_publisher.created_at is '作成日時';
comment on column tbl_publisher.updated_by is '更新者';
comment on column tbl_publisher.updated_at is '作成日時';

comment on table tbl_author is '著者';
comment on column tbl_author.author_id is '著者ID';
comment on column tbl_author.author_name is '著者名';
comment on column tbl_author.brief_history is '略歴';
comment on column tbl_author.valid_start_date is '有効開始日';
comment on column tbl_author.valid_end_date is '有効終了日';
comment on column tbl_author.created_by is '作成者';
comment on column tbl_author.created_at is '作成日時';
comment on column tbl_author.updated_by is '更新者';
comment on column tbl_author.updated_at is '更新日時';

