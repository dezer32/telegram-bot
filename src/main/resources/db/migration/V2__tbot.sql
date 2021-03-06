CREATE TABLE `currency` (
  `id` int(2) NOT NULL,
  `name_currency` varchar(3) NOT NULL
) CHARSET=utf8;

CREATE TABLE `exchange` (
  `id` int(11) NOT NULL,
  `id_cur_from` int(2) NOT NULL,
  `id_cur_to` int(2) NOT NULL,
  `value` float NOT NULL,
-- @TODO в базе данных дату нужно хранить в поле DATE
-- @TODO добавить новый файл, сделать в нем ALTER TABLE на это поле
  `date` varchar(8) NOT NULL
)  CHARSET=utf8;

CREATE TABLE `user` (
  `id` int(11) NOT NULL,
-- @TODO если в телеграме telegram_id число то лучше здесь сделать его числом тоже
  `telegram_id` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL
) CHARSET=utf8;

ALTER TABLE `currency`
  ADD PRIMARY KEY (`id`);

ALTER TABLE `exchange`
  ADD PRIMARY KEY (`id`);

ALTER TABLE `user`
  ADD PRIMARY KEY (`id`);

ALTER TABLE `currency`
  MODIFY `id` int(2) NOT NULL AUTO_INCREMENT;

ALTER TABLE `exchange`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

ALTER TABLE `user`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;