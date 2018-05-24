ALTER TABLE authentication_token ADD CONSTRAINT UQ_authentication_token UNIQUE(authentication_token);
ALTER TABLE survey_answer ADD CONSTRAINT UQ_survey_answer UNIQUE(saved_url);