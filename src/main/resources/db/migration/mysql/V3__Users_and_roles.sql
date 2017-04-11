CREATE TABLE `users` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(255) NOT NULL UNIQUE,
  `password` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE `user_roles` (
  `user_id` BIGINT(20) NOT NULL,
  `role` VARCHAR(255) NOT NULL,
  UNIQUE (`user_id`, `role`),
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`)
);

INSERT INTO `users` (`id`, `username`, `password`)
VALUES
  (1, 'reader', '$2a$10$RR.wwBjPmwm2oFa7poSLKutyUXIcw9iXmoWO26HsMmArhgJRx0tU.'),
  (2, 'writer', '$2a$10$P/pKGpOLJ5XyUMxSet8XN.dhxJnJrMf.7.GmCdtFBbRDotXfaYdpK'),
  (3, 'lelya', '$2a$10$cfrOrTpO12ItqXBEo30qTep0zMH02XluygsXq7YQRfFHgxEybUbHO');

INSERT INTO `user_roles`
VALUES
  (1, 'READER'),
  (2, 'READER'),
  (2, 'WRITER'),
  (3, 'READER'),
  (3, 'WRITER');
