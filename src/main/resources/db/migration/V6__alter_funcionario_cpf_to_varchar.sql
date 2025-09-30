-- V6: Converter T_MT_FUNCIONARIO.CD_CPF de NUMBER para VARCHAR2(20)
-- Idempotente:
--   - Se já for VARCHAR2, não faz nada.
--   - Se for NUMBER, recria a coluna via coluna temporária.

DECLARE
  v_data_type       VARCHAR2(30);
  v_unique_cons     VARCHAR2(200);
  v_stmt            VARCHAR2(4000);
BEGIN
  -- Verifica o tipo atual da coluna
  SELECT data_type INTO v_data_type
  FROM user_tab_columns
  WHERE table_name = 'T_MT_FUNCIONARIO'
    AND column_name = 'CD_CPF';

  IF v_data_type = 'VARCHAR2' THEN
    -- Já está do tipo correto, nada a fazer
    RETURN;
  END IF;

  -- 1. Localiza constraint UNIQUE sobre CD_CPF (se houver) para dropar
  BEGIN
    SELECT uc.constraint_name
      INTO v_unique_cons
      FROM user_constraints uc
      JOIN user_cons_columns ucc
        ON uc.constraint_name = ucc.constraint_name
     WHERE uc.table_name = 'T_MT_FUNCIONARIO'
       AND uc.constraint_type = 'U'
       AND ucc.column_name = 'CD_CPF'
       AND ROWNUM = 1;

    v_stmt := 'ALTER TABLE T_MT_FUNCIONARIO DROP CONSTRAINT ' || v_unique_cons;
    EXECUTE IMMEDIATE v_stmt;
  EXCEPTION
    WHEN NO_DATA_FOUND THEN
      NULL;
  END;

  -- 2. Cria coluna temporária
  EXECUTE IMMEDIATE 'ALTER TABLE T_MT_FUNCIONARIO ADD (CD_CPF_TXT VARCHAR2(20))';

  -- 3. Copia dados:
  -- Opção A (sem padding):
  EXECUTE IMMEDIATE 'UPDATE T_MT_FUNCIONARIO SET CD_CPF_TXT = TO_CHAR(CD_CPF)';

  -- Opção B (se quiser sempre 11 dígitos com zeros à esquerda):
  -- EXECUTE IMMEDIATE ''UPDATE T_MT_FUNCIONARIO SET CD_CPF_TXT = LPAD(TO_CHAR(CD_CPF),11,''0'')'';

  -- 4. Drop da coluna antiga
  EXECUTE IMMEDIATE 'ALTER TABLE T_MT_FUNCIONARIO DROP COLUMN CD_CPF';

  -- 5. Renomeia coluna temporária
  EXECUTE IMMEDIATE 'ALTER TABLE T_MT_FUNCIONARIO RENAME COLUMN CD_CPF_TXT TO CD_CPF';

  -- 6. Recria UNIQUE constraint (opcional, se quiser garantir unicidade)
  EXECUTE IMMEDIATE 'ALTER TABLE T_MT_FUNCIONARIO ADD CONSTRAINT UK_FUNCIONARIO_CPF UNIQUE (CD_CPF)';
END;
/