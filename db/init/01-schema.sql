CREATE TABLE IF NOT EXISTS completed_purchase (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(120) NOT NULL,
    full_name VARCHAR(120) NOT NULL,
    document_number VARCHAR(30) NOT NULL,
    operation_date VARCHAR(60) NOT NULL,
    transaction_id VARCHAR(80) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

DROP PROCEDURE IF EXISTS sp_complete_purchase;

DELIMITER //
CREATE PROCEDURE sp_complete_purchase(
    IN p_email VARCHAR(120),
    IN p_full_name VARCHAR(120),
    IN p_document_number VARCHAR(30),
    IN p_operation_date VARCHAR(60),
    IN p_transaction_id VARCHAR(80)
)
BEGIN
    INSERT INTO completed_purchase (
        email,
        full_name,
        document_number,
        operation_date,
        transaction_id
    ) VALUES (
        p_email,
        p_full_name,
        p_document_number,
        p_operation_date,
        p_transaction_id
    );

    SELECT '0' AS code, 'Compra registrada correctamente' AS message;
END //
DELIMITER ;
