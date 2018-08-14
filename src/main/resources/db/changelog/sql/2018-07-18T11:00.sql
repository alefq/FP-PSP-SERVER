ALTER TABLE security.termcondpol
  ADD COLUMN locale character varying(10) NOT NULL DEFAULT 'en_US';
