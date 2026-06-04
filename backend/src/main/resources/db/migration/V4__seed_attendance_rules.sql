INSERT INTO attendance_rules (average_wait_time, accepts_emergency, notes, specialty_id)
SELECT 20, true, 'Atendimento prioritário para sintomas cardíacos e dor torácica', id
FROM specialties
WHERE name = 'Cardiologia';

INSERT INTO attendance_rules (average_wait_time, accepts_emergency, notes, specialty_id)
SELECT 25, true, 'Encaminhar com prioridade em casos de déficit neurológico ou convulsão', id
FROM specialties
WHERE name = 'Neurologia';

INSERT INTO attendance_rules (average_wait_time, accepts_emergency, notes, specialty_id)
SELECT 30, true, 'Fraturas, dores intensas e traumas devem ser avaliados rapidamente', id
FROM specialties
WHERE name = 'Ortopedia';

INSERT INTO attendance_rules (average_wait_time, accepts_emergency, notes, specialty_id)
SELECT 15, true, 'Primeiro contato assistencial para sintomas inespecíficos', id
FROM specialties
WHERE name = 'Clínica Geral';

INSERT INTO attendance_rules (average_wait_time, accepts_emergency, notes, specialty_id)
SELECT 20, true, 'Prioridade para febre alta, falta de ar e desidratação', id
FROM specialties
WHERE name = 'Pediatria';

INSERT INTO attendance_rules (average_wait_time, accepts_emergency, notes, specialty_id)
SELECT 35, true, 'Sangramento intenso, dor pélvica aguda e gestação de risco exigem prioridade', id
FROM specialties
WHERE name = 'Ginecologia';

INSERT INTO attendance_rules (average_wait_time, accepts_emergency, notes, specialty_id)
SELECT 25, true, 'Dor abdominal aguda, sangramento e sinais de parto devem ser priorizados', id
FROM specialties
WHERE name = 'Obstetrícia';

INSERT INTO attendance_rules (average_wait_time, accepts_emergency, notes, specialty_id)
SELECT 40, false, 'Lesões de pele sem gravidade podem aguardar atendimento ambulatorial', id
FROM specialties
WHERE name = 'Dermatologia';

INSERT INTO attendance_rules (average_wait_time, accepts_emergency, notes, specialty_id)
SELECT 20, true, 'Alteração visual súbita, dor ocular intensa e trauma exigem prioridade', id
FROM specialties
WHERE name = 'Oftalmologia';

INSERT INTO attendance_rules (average_wait_time, accepts_emergency, notes, specialty_id)
SELECT 25, true, 'Crise respiratória, sangramento nasal persistente e perda auditiva súbita merecem prioridade', id
FROM specialties
WHERE name = 'Otorrinolaringologia';

INSERT INTO attendance_rules (average_wait_time, accepts_emergency, notes, specialty_id)
SELECT 30, false, 'Casos estáveis podem ser acompanhados em consulta programada', id
FROM specialties
WHERE name = 'Psiquiatria';

INSERT INTO attendance_rules (average_wait_time, accepts_emergency, notes, specialty_id)
SELECT 25, true, 'Crises metabólicas, glicemia muito alterada e sintomas hormonais intensos exigem avaliação', id
FROM specialties
WHERE name = 'Endocrinologia';

INSERT INTO attendance_rules (average_wait_time, accepts_emergency, notes, specialty_id)
SELECT 20, true, 'Dor abdominal forte, vômitos persistentes e sangramento digestivo devem ser priorizados', id
FROM specialties
WHERE name = 'Gastroenterologia';

INSERT INTO attendance_rules (average_wait_time, accepts_emergency, notes, specialty_id)
SELECT 20, true, 'Falta de ar, crise asmática e saturação baixa exigem atendimento prioritário', id
FROM specialties
WHERE name = 'Pneumologia';

INSERT INTO attendance_rules (average_wait_time, accepts_emergency, notes, specialty_id)
SELECT 25, true, 'Dor lombar intensa, retenção urinária e sangramento urinário pedem avaliação rápida', id
FROM specialties
WHERE name = 'Urologia';

INSERT INTO attendance_rules (average_wait_time, accepts_emergency, notes, specialty_id)
SELECT 30, true, 'Dor lombar com alteração de função renal ou edema requer prioridade', id
FROM specialties
WHERE name = 'Nefrologia';

INSERT INTO attendance_rules (average_wait_time, accepts_emergency, notes, specialty_id)
SELECT 35, false, 'Quadros articulares crônicos costumam ser avaliados em consulta programada', id
FROM specialties
WHERE name = 'Reumatologia';

INSERT INTO attendance_rules (average_wait_time, accepts_emergency, notes, specialty_id)
SELECT 20, true, 'Febre persistente, sinais de infecção grave e imunossupressão exigem prioridade', id
FROM specialties
WHERE name = 'Infectologia';

INSERT INTO attendance_rules (average_wait_time, accepts_emergency, notes, specialty_id)
SELECT 25, true, 'Anemia importante, sangramento e alterações hematológicas agudas devem ser priorizados', id
FROM specialties
WHERE name = 'Hematologia';

INSERT INTO attendance_rules (average_wait_time, accepts_emergency, notes, specialty_id)
SELECT 20, true, 'Sinais de alarme oncológico e dor intensa devem ser avaliados rapidamente', id
FROM specialties
WHERE name = 'Oncologia';

INSERT INTO attendance_rules (average_wait_time, accepts_emergency, notes, specialty_id)
SELECT 20, true, 'Dor em membros, suspeita de trombose e isquemia requerem prioridade', id
FROM specialties
WHERE name = 'Vascular';

INSERT INTO attendance_rules (average_wait_time, accepts_emergency, notes, specialty_id)
SELECT 25, true, 'Dor abdominal aguda, hérnias complicadas e abdome agudo devem ser priorizados', id
FROM specialties
WHERE name = 'Cirurgia Geral';

INSERT INTO attendance_rules (average_wait_time, accepts_emergency, notes, specialty_id)
SELECT 15, true, 'Casos cirúrgicos agudos e procedimentos de emergência demandam avaliação imediata', id
FROM specialties
WHERE name = 'Anestesiologia';

INSERT INTO attendance_rules (average_wait_time, accepts_emergency, notes, specialty_id)
SELECT 25, true, 'Convulsões, atraso do desenvolvimento e déficits neurológicos pediátricos requerem prioridade', id
FROM specialties
WHERE name = 'Neuropediatria';

INSERT INTO attendance_rules (average_wait_time, accepts_emergency, notes, specialty_id)
SELECT 30, true, 'Nódulos mamários dolorosos, secreção e sinais de alarme devem ser priorizados', id
FROM specialties
WHERE name = 'Mastologia';

INSERT INTO attendance_rules (average_wait_time, accepts_emergency, notes, specialty_id)
SELECT 35, false, 'Avaliação nutricional é, em geral, eletiva e orientada por acompanhamento', id
FROM specialties
WHERE name = 'Nutrologia';
