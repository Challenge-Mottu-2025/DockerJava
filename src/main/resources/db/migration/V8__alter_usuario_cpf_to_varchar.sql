-- V8: Converter PK T_MT_USUARIO.CD_CPF de NUMBER -> VARCHAR2(20) de forma idempotente
-- Estratégia:
--   - Se já for VARCHAR2, nada a fazer.
--   - Se NUMBER:
--       1. Captura nome da PK
--       2. Cria coluna temporária CD_CPF_TXT
--       3. Copia valores (sem padding ou com LPAD opcional)
--       4. Drop da PK
--       5. Drop da coluna antiga
--       6. Renomeia temporária para CD_CPF
--       7. Recria PK
--       8. (Opcional) Constraint de formato

DECLARE
  v_data_type     VARCHAR2(30);
  v_pk_name       VARCHAR2(200);
  v_has_pk        INTEGER;
BEGIN
  -- Tipo atual
  SELECT data_type INTO v_data_type
  FROM user_tab_columns
  WHERE table_name='T_MT_USUARIO'
    AND column_name='CD_CPF';

  IF v_data_type = 'VARCHAR2' THEN
    -- Já convertido
    RETURN;
  END IF;

  -- Nome da PK
  SELECT COUNT(*) INTO v_has_pk
  FROM user_constraints
  WHERE table_name='T_MT_USUARIO' AND constraint_type='P';

  IF v_has_pk > 0 THEN
    SELECT constraint_name INTO v_pk_name
    FROM user_constraints
    WHERE table_name='T_MT_USUARIO' AND constraint_type='P';
  END IF;

  -- Coluna temporária
  EXECUTE IMMEDIATE 'ALTER TABLE T_MT_USUARIO ADD (CD_CPF_TXT VARCHAR2(20))';

  -- Copiar dados
  -- Opção A (sem padding)
  EXECUTE IMMEDIATE 'UPDATE T_MT_USUARIO SET CD_CPF_TXT = TO_CHAR(CD_CPF)';
  -- Opção B (padding 11 dígitos) - descomente se quiser fixar 11:
  -- EXECUTE IMMEDIATE q'[UPDATE T_MT_USUARIO SET CD_CPF_TXT = LPAD(TO_CHAR(CD_CPF),11,'0')]';

  -- Drop PK (se existia)
  IF v_has_pk > 0 THEN
    EXECUTE IMMEDIATE 'ALTER TABLE T_MT_USUARIO DROP CONSTRAINT '||v_pk_name;
  END IF;

  -- Drop coluna NUMBER original
  EXECUTE IMMEDIATE 'ALTER TABLE T_MT_USUARIO DROP COLUMN CD_CPF';

  -- Renomear temporária
  EXECUTE IMMEDIATE 'ALTER TABLE T_MT_USUARIO RENAME COLUMN CD_CPF_TXT TO CD_CPF';

  -- Recriar PK
  EXECUTE IMMEDIATE 'ALTER TABLE T_MT_USUARIO ADD CONSTRAINT PK_T_MT_USUARIO PRIMARY KEY (CD_CPF)';

  -- (Opcional) RECRIA constraint de formato (apenas dígitos) - descomente se quiser:
  -- EXECUTE IMMEDIATE q'[ALTER TABLE T_MT_USUARIO ADD CONSTRAINT CK_USUARIO_CPF_FMT CHECK (REGEXP_LIKE(CD_CPF,'^\d{11}$'))]';

END;
/