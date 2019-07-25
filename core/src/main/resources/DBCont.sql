
CREATE DATABASE IF NOT EXISTS `DBCont` CHARACTER SET 'utf8' COLLATE 'utf8_general_ci';
USE `DBCont` ;

-- -----------------------------------------------------
-- Table `DBCont`.`address`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `DBCont`.`address` (
    `addressId` INT(11) NOT NULL AUTO_INCREMENT,
    `country` VARCHAR(45) NULL DEFAULT NULL,
    `city` VARCHAR(45) NULL DEFAULT NULL,
    `street` VARCHAR(60) NULL DEFAULT NULL,
    `houseNumber` VARCHAR(45) NULL DEFAULT NULL,
    `flat` VARCHAR(45) NULL DEFAULT NULL,
    `zipcode` VARCHAR(45) NULL DEFAULT NULL,
PRIMARY KEY (`addressId`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = UTF8;


-- -----------------------------------------------------
-- Table `DBCont`.`contact`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `DBCont`.`contact` (
    `contactId` INT(11) NOT NULL AUTO_INCREMENT,
    `firstName` VARCHAR(100) CHARACTER SET 'utf8mb4' NOT NULL,
    `lastName` VARCHAR(100) CHARACTER SET 'utf8mb4' NOT NULL,
    `patronymic` VARCHAR(100) CHARACTER SET 'utf8mb4' NULL DEFAULT NULL,
    `birthday` TIMESTAMP NULL DEFAULT NULL,
    `gender` VARCHAR(45) CHARACTER SET 'utf8mb4' NULL DEFAULT NULL,
    `citizenship` VARCHAR(45) CHARACTER SET 'utf8mb4' NULL DEFAULT NULL,
    `maritalStatus` VARCHAR(45) CHARACTER SET 'utf8mb4' NULL DEFAULT NULL,
    `website` VARCHAR(200) CHARACTER SET 'utf8mb4' NULL DEFAULT NULL,
    `email` VARCHAR(100) CHARACTER SET 'utf8mb4' NOT NULL,
    `company` VARCHAR(100) CHARACTER SET 'utf8mb4' NULL DEFAULT NULL,
    `addressId` INT(11) NULL DEFAULT NULL,
    `photo` VARCHAR(2000) CHARACTER SET 'utf8mb4' NULL DEFAULT NULL,
PRIMARY KEY (`contactId`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = UTF8;


-- -----------------------------------------------------
-- Table `DBCont`.`attachments`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `DBCont`.`attachments` (
    `attachmentsId` INT(11) NOT NULL AUTO_INCREMENT,
    `filename` VARCHAR(200) NULL DEFAULT NULL,
    `dateOfLoad` DATE NULL DEFAULT NULL,
    `comment` VARCHAR(2000) NULL DEFAULT NULL,
     `contactId` INT(11) NOT NULL,
    `randomFileName` VARCHAR(45) NULL DEFAULT NULL,
PRIMARY KEY (`attachmentsId`),
INDEX `fk_attachments_contact1_idx` (`contactId` ASC),
CONSTRAINT `fk_attachments_contact1`
FOREIGN KEY (`contactId`)
REFERENCES `DBCont`.`contact` (`contactId`)
ON DELETE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = UTF8;


-- -----------------------------------------------------
-- Table `DBCont`.`phone`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `DBCont`.`phone` (
    `phoneId` INT(11) NOT NULL AUTO_INCREMENT,
    `countryCode` INT(11) NULL DEFAULT NULL,
    `operatorCode` INT(11) NULL DEFAULT NULL,
    `number` VARCHAR(45) NULL DEFAULT NULL,
    `typePhone` VARCHAR(45) NULL DEFAULT NULL,
    `comment` VARCHAR(2000) NULL DEFAULT NULL,
    `contactId` INT(11) NOT NULL,
PRIMARY KEY (`phoneId`),
INDEX `fk_phone_contact1_idx` (`contactId` ASC),
CONSTRAINT `fk_phone_contact1`
FOREIGN KEY (`contactId`)
REFERENCES `DBCont`.`contact` (`contactId`)
ON DELETE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = UTF8;