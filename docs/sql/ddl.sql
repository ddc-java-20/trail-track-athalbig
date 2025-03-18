-- Generated 2025-03-17 14:27:02-0600 for database version 1

CREATE TABLE IF NOT EXISTS `pin`
(
    `pin_id`      INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    `title`       TEXT                              NOT NULL COLLATE NOCASE,
    `content`     TEXT                              NOT NULL,
    `image`       TEXT,
    `created_on`  INTEGER                           NOT NULL,
    `modified_on` INTEGER                           NOT NULL,
    `latitude`    REAL                              NOT NULL,
    `longitude`   REAL                              NOT NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS `index_pin_title` ON `pin` (`title`);

CREATE INDEX IF NOT EXISTS `index_pin_created_on` ON `pin` (`created_on`);

CREATE INDEX IF NOT EXISTS `index_pin_modified_on` ON `pin` (`modified_on`);

CREATE TABLE IF NOT EXISTS `track`
(
    `track_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL
);

CREATE TABLE IF NOT EXISTS `user`
(
    `user_id`      INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    `display_name` TEXT                              NOT NULL COLLATE NOCASE,
    `oauth_key`    TEXT                              NOT NULL COLLATE NOCASE,
    `created`      INTEGER                           NOT NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS `index_user_oauth_key` ON `user` (`oauth_key`);