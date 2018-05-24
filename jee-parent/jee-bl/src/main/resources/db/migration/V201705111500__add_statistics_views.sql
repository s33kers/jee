CREATE OR REPLACE VIEW QUESTION_STATISTICS (QUESTION_ID, ANSWER, ANSWER_COUNT) AS
  SELECT
    bq.id AS QUESTION_ID,
    qa.answer AS ANSWER,
    count(qa.answer) AS ANSWER_COUNT
  FROM survey s
    LEFT OUTER JOIN page p ON s.id = p.survey_id
    LEFT OUTER JOIN base_question bq ON p.id = bq.page_id
    LEFT OUTER JOIN question_answer qa ON bq.id = qa.question_id
  WHERE qa.answer IS NOT NULL AND qa.survey_answer_id IS NULL
  GROUP BY bq.id, bq.question, qa.answer
  UNION
  SELECT
    bq.id AS QUESTION_ID,
    cqa.answer AS ANSWER,
    count(cqa.answer) AS ANSWER_COUNT
  FROM survey s
    LEFT OUTER JOIN page p ON s.id = p.survey_id
    LEFT OUTER JOIN base_question bq ON p.id = bq.page_id
    LEFT OUTER JOIN question_answer qa ON bq.id = qa.question_id
    LEFT OUTER JOIN multiple_answers ma ON qa.id = ma.question_answer_id
    LEFT OUTER JOIN choose_question_answer cqa ON ma.choose_question_answer_id = cqa.id
  WHERE qa.answer IS NULL AND qa.survey_answer_id IS NULL
  GROUP BY bq.id, bq.question, cqa.answer;

CREATE OR REPLACE VIEW SURVEY_STATISTICS (SURVEY_ID, QUESTION_TYPE, QUESTION, QUESTION_ID, USER_ID, PUBLIC_RESULTS) AS
  SELECT
    s.id AS SURVEY_ID,
    bq.question_type AS QUESTION_TYPE,
    bq.question AS QUESTION,
    bq.id AS QUESTION_ID,
    s.user_id AS USER_ID,
    s.public_results AS PUBLIC_RESULTS
  FROM survey s
    LEFT OUTER JOIN page p ON s.id = p.survey_id
    LEFT OUTER JOIN base_question bq ON p.id = bq.page_id;