
INSERT INTO `customer_detail` (`id`, `first_name`, `last_name`, `email`, `phone_number`, `account_verified`) VALUES
    (2, 'Mykola', 'Mykola', 'kolya5179596@gmail.com', '+48791863388', b'1'),
    (3, 'Vasyl', 'Vynnychuk', 'nykolaichuk.mykola.m@gmail.com', '+48791863380', b'1'),
    (4, 'Yurii', 'Prodan', 'kolyanykolaichuk@gmail.com', '+48791863381', b'1');

INSERT INTO `customer` (`id`, `username`, `password`, `customer_detail_id`) VALUES
    (2, 'mykola_nykolaichuk', '$2a$10$Bibt1VjCQP1jIVTPBxsf4Om6nuZ7HW40tB7yBYWWNhBDMFu/RCCQi', 2),
    (3, 'vasyl_vynnychuk', '$2a$10$SXxxBg8mm74nJ.FW0lJAVu4nX62n6fvtk3XTsKlbfSWdIyTBi7YUe', 3),
    (4, 'yurii_prodan', '$2a$10$ETvcymFXlzwZEGCC509nRe5uU1E8VC5jniLBHQcFJT13keHr/6.b2', 4);

INSERT INTO `customer_authority` (`customer_id`, `authority_id`) VALUES
    (2, 1),
    (3, 1),
    (4, 1);

INSERT INTO `workshop` (`id`, `username`, `password`, `name`, `address`, `phone_number`, `email`, `account_verified`, `city_id`) VALUES
    (1, 'm_ka_motors', '$2a$10$sOyAe9JiSKn7cXuIJyanqOKjPyFE7kZ4NugQLvODDC5u92UMxq2zS', 'M-ka', 'ul. Bławatkowa 18', '+48791863383', 'mnykolaichuk96@gmail.com', b'1', 1),
    (2, 'BoschService', '$2a$10$w.cRoL0Oy4WVDbyYb0wHfOp4DdNn4iLOpG3eBIjSViaOqFNr7XLA.', 'Bosch Service', 'ul. Bławatkowa 18', '+48791863384', '150174@stud.prz.edu.pl', b'1', 1);

INSERT INTO `workshop_authority` (`workshop_id`, `authority_id`) VALUES
    (1, 2),
    (2, 2);
