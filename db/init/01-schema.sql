CREATE TABLE IF NOT EXISTS completed_purchase (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(120) NOT NULL,
    full_name VARCHAR(120) NOT NULL,
    document_number VARCHAR(30) NOT NULL,
    operation_date VARCHAR(60) NOT NULL,
    transaction_id VARCHAR(80) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

DROP FUNCTION IF EXISTS sp_complete_purchase(VARCHAR, VARCHAR, VARCHAR, VARCHAR, VARCHAR);

CREATE OR REPLACE FUNCTION sp_complete_purchase(
    p_email VARCHAR(120),
    p_full_name VARCHAR(120),
    p_document_number VARCHAR(30),
    p_operation_date VARCHAR(60),
    p_transaction_id VARCHAR(80)
)
RETURNS TABLE(code TEXT, message TEXT)
LANGUAGE plpgsql
AS $$
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

    RETURN QUERY SELECT '0'::TEXT, 'Compra registrada correctamente'::TEXT;
END;
$$;
