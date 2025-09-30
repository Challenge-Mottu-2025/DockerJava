-- V7: Converter T_MT_MOTO.CD_CPF de NUMBER para VARCHAR2(20) (ou 11 se preferir)
-- Idempotente. Similar à conversão feita em T_MT_FUNCIONARIO.

DECLARE
  v_data_type    VARCHAR2(30);
  v_unique_cons  VARCHAR2(200);
BEGIN
  -- Verifica tipo atual
  SELECT data_type INTO v_data_type
  FROM user_tab_columns
  WHERE table_name = 'T_MT_MOTO'
    AND column_name = 'CD_CPF';

  IF v_data_type = 'VARCHAR2' THEN
    -- Já convertido
    RETURN;
  END IF;

  -- 1. Dropar constraint UNIQUE se existir
  BEGIN
    SELECT uc.constraint_name
      INTO v_unique_cons
      FROM user_constraints uc
      JOIN user_cons_columns ucc ON uc.constraint_name = ucc.constraint_name
     WHERE uc.table_name='T_MT_MOTO'
       AND uc.constraint_type='U'
       AND ucc.column_name='CD_CPF'
       AND ROWNUM=1;

    EXECUTE IMMEDIATE 'ALTER TABLE T_MT_MOTO DROP CONSTRAINT ' || v_unique_cons;
  EXCEPTION
    WHEN NO_DATA_FOUND THEN
      NULL;
  END;

  -- 2. Criar coluna temporária
  EXECUTE IMMEDIATE 'ALTER TABLE T_MT_MOTO ADD (CD_CPF_TXT VARCHAR2(20))';

  -- 3. Copiar valores
  -- Opção A (sem padding)
  EXECUTE IMMEDIATE 'UPDATE T_MT_MOTO SET CD_CPF_TXT = TO_CHAR(CD_CPF)';
  -- Opção B (padding para 11 dígitos) - descomente se quiser pad:
  -- EXECUTE IMMEDIATE q'[UPDATE T_MT_MOTO SET CD_CPF_TXT = CASE WHEN CD_CPF IS NOT NULL THEN LPAD(TO_CHAR(CD_CPF),11,'0') END]';

  -- 4. Dropar coluna original
  EXECUTE IMMEDIATE 'ALTER TABLE T_MT_MOTO DROP COLUMN CD_CPF';

  -- 5. Renomear temporária
  EXECUTE IMMEDIATE 'ALTER TABLE T_MT_MOTO RENAME COLUMN CD_CPF_TXT TO CD_CPF';

  -- 6. (Opcional) Recriar UNIQUE sobre CD_CPF
  -- EXECUTE IMMEDIATE 'ALTER TABLE T_MT_MOTO ADD CONSTRAINT UK_MOTO_CPF UNIQUE (CD_CPF)';
END;
/