-- 1. INDEPENDENT MASTER TABLES

CREATE TABLE IF NOT EXISTS `countries` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `about_country_content` text,
  `about_country_title` varchar(255) DEFAULT NULL,
  `admission_process_intro` text,
  `admission_process_title` varchar(255) DEFAULT NULL,
  `application_deadline_note` varchar(255) DEFAULT NULL,
  `avg_monthly_living_usd` double DEFAULT NULL,
  `banner` varchar(255) DEFAULT NULL,
  `benefits_title` varchar(255) DEFAULT NULL,
  `canonical_url` varchar(255) DEFAULT NULL,
  `climate_overview` varchar(255) DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `documents_title` varchar(255) DEFAULT NULL,
  `eligibility_intro` text,
  `eligibility_title` varchar(255) DEFAULT NULL,
  `faq_title` varchar(255) DEFAULT NULL,
  `fee_structure_intro` text,
  `fee_structure_title` varchar(255) DEFAULT NULL,
  `flag` varchar(255) DEFAULT NULL,
  `fmge_intro` text,
  `fmge_note` varchar(255) DEFAULT NULL,
  `fmge_title` varchar(255) DEFAULT NULL,
  `health_insurance_requirement` varchar(255) DEFAULT NULL,
  `hero_subtitle` varchar(255) DEFAULT NULL,
  `hero_title` varchar(255) DEFAULT NULL,
  `hostel_intro` text,
  `hostel_title` varchar(255) DEFAULT NULL,
  `is_active` bit(1) NOT NULL,
  `is_pr_path_available` bit(1) DEFAULT NULL,
  `keywords` varchar(255) DEFAULT NULL,
  `licensing_exam_note` varchar(255) DEFAULT NULL,
  `living_cost_intro` text,
  `living_cost_title` varchar(255) DEFAULT NULL,
  `max_annual_tuition_usd` double DEFAULT NULL,
  `meta_description` text,
  `meta_title` varchar(255) DEFAULT NULL,
  `min_annual_tuition_usd` double DEFAULT NULL,
  `minimum_language_score_note` varchar(255) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `nmc_approved_title` varchar(255) DEFAULT NULL,
  `og_description` text,
  `og_image` varchar(255) DEFAULT NULL,
  `og_title` varchar(255) DEFAULT NULL,
  `overview` text,
  `part_time_work_allowed_hours` varchar(255) DEFAULT NULL,
  `popular_destinations_title` varchar(255) DEFAULT NULL,
  `post_study_work_rights` varchar(255) DEFAULT NULL,
  `requires_blocked_account` bit(1) DEFAULT NULL,
  `safety_index_rating` varchar(255) DEFAULT NULL,
  `scholarship_intro` text,
  `scholarship_title` varchar(255) DEFAULT NULL,
  `show_connect_with_counselor` bit(1) NOT NULL,
  `show_get_free_consultation` bit(1) NOT NULL,
  `slug` varchar(255) NOT NULL,
  `syllabus_intro` text,
  `syllabus_title` varchar(255) DEFAULT NULL,
  `things_to_consider_intro` text,
  `things_to_consider_title` varchar(255) DEFAULT NULL,
  `top_universities_intro` text,
  `top_universities_title` varchar(255) DEFAULT NULL,
  `total_international_students` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) NOT NULL,
  `visa_success_rate` varchar(255) DEFAULT NULL,
  `why_study_intro` text,
  `why_study_title` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_countries_name` (`name`),
  UNIQUE KEY `uk_countries_slug` (`slug`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE IF NOT EXISTS `courses` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `application_deadline` varchar(255) DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `degree_type` varchar(255) DEFAULT NULL,
  `description` text,
  `duration` varchar(255) DEFAULT NULL,
  `eligibility` text,
  `fee_currency` varchar(255) DEFAULT NULL,
  `field` varchar(255) DEFAULT NULL,
  `full_name` varchar(255) DEFAULT NULL,
  `image` varchar(255) DEFAULT NULL,
  `is_active` bit(1) NOT NULL,
  `keywords` varchar(255) DEFAULT NULL,
  `max_tuition_fee` double DEFAULT NULL,
  `max_tuition_usd` double DEFAULT NULL,
  `meta_description` text,
  `meta_title` varchar(255) DEFAULT NULL,
  `min_tuition_fee` double DEFAULT NULL,
  `min_tuition_usd` double DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `og_image` varchar(255) DEFAULT NULL,
  `popularity_score` int DEFAULT NULL,
  `rating` double DEFAULT NULL,
  `slug` varchar(255) NOT NULL,
  `study_level` varchar(255) DEFAULT NULL,
  `tuition_fee_native` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) NOT NULL,
  `view_count` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_courses_slug` (`slug`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE IF NOT EXISTS `exams` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `code` varchar(255) NOT NULL,
  `conducting_body` varchar(255) DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `description` text,
  `exam_mode` varchar(255) DEFAULT NULL,
  `is_active` bit(1) NOT NULL,
  `keywords` varchar(255) DEFAULT NULL,
  `meta_description` text,
  `meta_title` varchar(255) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `official_website_url` varchar(255) DEFAULT NULL,
  `og_image` varchar(255) DEFAULT NULL,
  `score_validity` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_exams_code` (`code`),
  UNIQUE KEY `uk_exams_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE IF NOT EXISTS `admin_users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `email` varchar(255) NOT NULL,
  `is_active` bit(1) NOT NULL,
  `name` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_admin_users_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- 2. PRIMARY DEPENDENT TABLES (Level 1)

CREATE TABLE IF NOT EXISTS `universities` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `application_deadline` varchar(255) DEFAULT NULL,
  `baseline_eligibility` varchar(255) DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `description` text,
  `established` int DEFAULT NULL,
  `estimated_monthly_living_usd` double DEFAULT NULL,
  `fee_currency` varchar(255) DEFAULT NULL,
  `has_international_mess` bit(1) DEFAULT NULL,
  `has_on_campus_hostel` bit(1) DEFAULT NULL,
  `image` varchar(255) DEFAULT NULL,
  `is_active` bit(1) NOT NULL,
  `keywords` varchar(255) DEFAULT NULL,
  `logo` varchar(255) DEFAULT NULL,
  `max_tuition_fee` double DEFAULT NULL,
  `max_tuition_usd` double DEFAULT NULL,
  `meta_description` text,
  `meta_title` varchar(255) DEFAULT NULL,
  `min_tuition_fee` double DEFAULT NULL,
  `min_tuition_usd` double DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `og_image` varchar(255) DEFAULT NULL,
  `popularity_score` int DEFAULT NULL,
  `ranking` int DEFAULT NULL,
  `rating` double DEFAULT NULL,
  `slug` varchar(255) NOT NULL,
  `tuition_fee_native` varchar(255) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) NOT NULL,
  `view_count` int DEFAULT NULL,
  `country_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_universities_slug` (`slug`),
  KEY `idx_universities_country_id` (`country_id`),
  CONSTRAINT `fk_universities_country` FOREIGN KEY (`country_id`) REFERENCES `countries` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE IF NOT EXISTS `colleges` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `application_deadline` varchar(255) DEFAULT NULL,
  `baseline_eligibility` varchar(255) DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `description` text,
  `established` int DEFAULT NULL,
  `estimated_monthly_living_usd` double DEFAULT NULL,
  `fee_currency` varchar(255) DEFAULT NULL,
  `has_international_mess` bit(1) DEFAULT NULL,
  `has_on_campus_hostel` bit(1) DEFAULT NULL,
  `image` varchar(255) DEFAULT NULL,
  `is_active` bit(1) NOT NULL,
  `keywords` varchar(255) DEFAULT NULL,
  `logo` varchar(255) DEFAULT NULL,
  `max_tuition_fee` double DEFAULT NULL,
  `max_tuition_usd` double DEFAULT NULL,
  `meta_description` text,
  `meta_title` varchar(255) DEFAULT NULL,
  `min_tuition_fee` double DEFAULT NULL,
  `min_tuition_usd` double DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `og_image` varchar(255) DEFAULT NULL,
  `popularity_score` int DEFAULT NULL,
  `ranking` int DEFAULT NULL,
  `rating` double DEFAULT NULL,
  `slug` varchar(255) NOT NULL,
  `tuition_fee_native` varchar(255) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) NOT NULL,
  `view_count` int DEFAULT NULL,
  `country_id` bigint NOT NULL,
  `university_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_colleges_slug` (`slug`),
  KEY `idx_colleges_country_id` (`country_id`),
  KEY `idx_colleges_university_id` (`university_id`),
  CONSTRAINT `fk_colleges_country` FOREIGN KEY (`country_id`) REFERENCES `countries` (`id`),
  CONSTRAINT `fk_colleges_university` FOREIGN KEY (`university_id`) REFERENCES `universities` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- 3. MAPPINGS, ATTRIBUTES & SECONDARY TABLES

CREATE TABLE IF NOT EXISTS `admin_user_roles` (
  `user_id` bigint NOT NULL,
  `role` enum('ADMIN','EDITOR','SUPER_ADMIN','VIEWER') DEFAULT NULL,
  UNIQUE KEY `uk_admin_user_roles_user_role` (`user_id`,`role`),
  CONSTRAINT `fk_admin_user_roles_user` FOREIGN KEY (`user_id`) REFERENCES `admin_users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE IF NOT EXISTS `applications` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `applicant_email` varchar(255) NOT NULL,
  `counselor_remarks` text,
  `created_at` datetime(6) NOT NULL,
  `status` enum('CONDITIONAL_OFFER','DOCUMENT_VERIFICATION','ENROLLED','REJECTED','SUBMITTED','UNCONDITIONAL_OFFER','VISA_APPROVED','VISA_PROCESSING') NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `course_id` bigint NOT NULL,
  `university_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_applications_course_id` (`course_id`),
  KEY `idx_applications_university_id` (`university_id`),
  CONSTRAINT `fk_applications_course` FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`),
  CONSTRAINT `fk_applications_university` FOREIGN KEY (`university_id`) REFERENCES `universities` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE IF NOT EXISTS `application_documents` (
  `application_id` bigint NOT NULL,
  `document_url` varchar(255) DEFAULT NULL,
  KEY `idx_app_docs_application_id` (`application_id`),
  CONSTRAINT `fk_application_documents_application` FOREIGN KEY (`application_id`) REFERENCES `applications` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE IF NOT EXISTS `college_accreditations` (
  `college_id` bigint NOT NULL,
  `accreditation` varchar(255) DEFAULT NULL,
  KEY `idx_college_accreditations_college_id` (`college_id`),
  CONSTRAINT `fk_college_accreditations_college` FOREIGN KEY (`college_id`) REFERENCES `colleges` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE IF NOT EXISTS `college_available_intakes` (
  `college_id` bigint NOT NULL,
  `intake` varchar(255) DEFAULT NULL,
  KEY `idx_college_avail_intakes_college_id` (`college_id`),
  CONSTRAINT `fk_college_avail_intakes_college` FOREIGN KEY (`college_id`) REFERENCES `colleges` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE IF NOT EXISTS `college_course_mapping` (
  `college_id` bigint NOT NULL,
  `course_id` bigint NOT NULL,
  KEY `idx_college_course_map_course_id` (`course_id`),
  KEY `idx_college_course_map_college_id` (`college_id`),
  CONSTRAINT `fk_college_course_mapping_college` FOREIGN KEY (`college_id`) REFERENCES `colleges` (`id`),
  CONSTRAINT `fk_college_course_mapping_course` FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE IF NOT EXISTS `college_facilities` (
  `college_id` bigint NOT NULL,
  `facility` varchar(255) DEFAULT NULL,
  KEY `idx_college_facilities_college_id` (`college_id`),
  CONSTRAINT `fk_college_facilities_college` FOREIGN KEY (`college_id`) REFERENCES `colleges` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE IF NOT EXISTS `country_about_attributes` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `attribute` varchar(255) DEFAULT NULL,
  `detail` varchar(255) DEFAULT NULL,
  `country_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_country_about_attrs_country_id` (`country_id`),
  CONSTRAINT `fk_country_about_attributes_country` FOREIGN KEY (`country_id`) REFERENCES `countries` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE IF NOT EXISTS `country_accepted_language_tests` (
  `country_id` bigint NOT NULL,
  `test_name` varchar(255) DEFAULT NULL,
  KEY `idx_country_lang_tests_country_id` (`country_id`),
  CONSTRAINT `fk_country_lang_tests_country` FOREIGN KEY (`country_id`) REFERENCES `countries` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE IF NOT EXISTS `country_admission_steps` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `content` text,
  `step_order` int NOT NULL,
  `title` varchar(255) DEFAULT NULL,
  `country_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_country_adm_steps_country_id` (`country_id`),
  CONSTRAINT `fk_country_admission_steps_country` FOREIGN KEY (`country_id`) REFERENCES `countries` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE IF NOT EXISTS `country_available_intakes` (
  `country_id` bigint NOT NULL,
  `intake` varchar(255) DEFAULT NULL,
  KEY `idx_country_avail_intakes_country_id` (`country_id`),
  CONSTRAINT `fk_country_avail_intakes_country` FOREIGN KEY (`country_id`) REFERENCES `countries` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE IF NOT EXISTS `country_benefits` (
  `country_id` bigint NOT NULL,
  `benefit` text,
  KEY `idx_country_benefits_country_id` (`country_id`),
  CONSTRAINT `fk_country_benefits_country` FOREIGN KEY (`country_id`) REFERENCES `countries` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE IF NOT EXISTS `country_destinations` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `flag` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `slug` varchar(255) DEFAULT NULL,
  `country_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_country_destinations_country_id` (`country_id`),
  CONSTRAINT `fk_country_destinations_country` FOREIGN KEY (`country_id`) REFERENCES `countries` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE IF NOT EXISTS `country_documents` (
  `country_id` bigint NOT NULL,
  `document` varchar(255) DEFAULT NULL,
  KEY `idx_country_documents_country_id` (`country_id`),
  CONSTRAINT `fk_country_documents_country` FOREIGN KEY (`country_id`) REFERENCES `countries` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE IF NOT EXISTS `country_eligibility` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `detail` varchar(255) DEFAULT NULL,
  `requirement` varchar(255) DEFAULT NULL,
  `country_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_country_eligibility_country_id` (`country_id`),
  CONSTRAINT `fk_country_eligibility_country` FOREIGN KEY (`country_id`) REFERENCES `countries` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE IF NOT EXISTS `country_faqs` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `answer` text,
  `question` text,
  `country_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_country_faqs_country_id` (`country_id`),
  CONSTRAINT `fk_country_faqs_country` FOREIGN KEY (`country_id`) REFERENCES `countries` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE IF NOT EXISTS `country_fee_structure` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `annual_hostel_usd` double DEFAULT NULL,
  `annual_tuition_usd` double DEFAULT NULL,
  `hostel_fee_native` varchar(255) DEFAULT NULL,
  `native_currency_code` varchar(10) DEFAULT NULL,
  `tuition_fee_native` varchar(255) DEFAULT NULL,
  `university_name` varchar(255) DEFAULT NULL,
  `country_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_country_fee_structure_country_id` (`country_id`),
  CONSTRAINT `fk_country_fee_structure_country` FOREIGN KEY (`country_id`) REFERENCES `countries` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE IF NOT EXISTS `country_fmge_rates` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `pass_rate` varchar(255) DEFAULT NULL,
  `university` varchar(255) DEFAULT NULL,
  `country_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_country_fmge_rates_country_id` (`country_id`),
  CONSTRAINT `fk_country_fmge_rates_country` FOREIGN KEY (`country_id`) REFERENCES `countries` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE IF NOT EXISTS `country_gallery` (
  `country_id` bigint NOT NULL,
  `image_url` text,
  KEY `idx_country_gallery_country_id` (`country_id`),
  CONSTRAINT `fk_country_gallery_country` FOREIGN KEY (`country_id`) REFERENCES `countries` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE IF NOT EXISTS `country_highlights` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `detail` varchar(255) DEFAULT NULL,
  `feature` varchar(255) DEFAULT NULL,
  `country_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_country_highlights_country_id` (`country_id`),
  CONSTRAINT `fk_country_highlights_country` FOREIGN KEY (`country_id`) REFERENCES `countries` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE IF NOT EXISTS `country_hostel_facilities` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `criteria` varchar(255) DEFAULT NULL,
  `information` varchar(255) DEFAULT NULL,
  `country_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_country_hostel_fac_country_id` (`country_id`),
  CONSTRAINT `fk_country_hostel_fac_country` FOREIGN KEY (`country_id`) REFERENCES `countries` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE IF NOT EXISTS `country_living_costs` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `expense_category` varchar(255) DEFAULT NULL,
  `monthly_cost` varchar(255) DEFAULT NULL,
  `country_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_country_living_costs_country_id` (`country_id`),
  CONSTRAINT `fk_country_living_costs_country` FOREIGN KEY (`country_id`) REFERENCES `countries` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE IF NOT EXISTS `country_scholarships` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `description` text,
  `name` varchar(255) DEFAULT NULL,
  `country_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_country_scholarships_country_id` (`country_id`),
  CONSTRAINT `fk_country_scholarships_country` FOREIGN KEY (`country_id`) REFERENCES `countries` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE IF NOT EXISTS `country_semesters` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `semester` varchar(255) DEFAULT NULL,
  `country_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_country_semesters_country_id` (`country_id`),
  CONSTRAINT `fk_country_semesters_country` FOREIGN KEY (`country_id`) REFERENCES `countries` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE IF NOT EXISTS `country_semester_subjects` (
  `country_semester_id` bigint NOT NULL,
  `subject` varchar(255) DEFAULT NULL,
  KEY `idx_country_sem_subjects_sem_id` (`country_semester_id`),
  CONSTRAINT `fk_country_sem_subjects_semester` FOREIGN KEY (`country_semester_id`) REFERENCES `country_semesters` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE IF NOT EXISTS `country_things_to_consider` (
  `country_id` bigint NOT NULL,
  `point` text,
  KEY `idx_country_things_consider_country_id` (`country_id`),
  CONSTRAINT `fk_country_things_consider_country` FOREIGN KEY (`country_id`) REFERENCES `countries` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE IF NOT EXISTS `country_why_study_points` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `content` text,
  `title` varchar(255) DEFAULT NULL,
  `country_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_country_why_study_country_id` (`country_id`),
  CONSTRAINT `fk_country_why_study_country` FOREIGN KEY (`country_id`) REFERENCES `countries` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE IF NOT EXISTS `course_available_intakes` (
  `course_id` bigint NOT NULL,
  `intake` varchar(255) DEFAULT NULL,
  KEY `idx_course_avail_intakes_course_id` (`course_id`),
  CONSTRAINT `fk_course_avail_intakes_course` FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE IF NOT EXISTS `course_career_prospects` (
  `course_id` bigint NOT NULL,
  `prospect` varchar(255) DEFAULT NULL,
  KEY `idx_course_career_prospects_course_id` (`course_id`),
  CONSTRAINT `fk_course_career_prospects_course` FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE IF NOT EXISTS `course_required_exams` (
  `course_id` bigint NOT NULL,
  `exam_name` varchar(255) DEFAULT NULL,
  KEY `idx_course_req_exams_course_id` (`course_id`),
  CONSTRAINT `fk_course_req_exams_course` FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE IF NOT EXISTS `course_subjects` (
  `course_id` bigint NOT NULL,
  `subject` varchar(255) DEFAULT NULL,
  KEY `idx_course_subjects_course_id` (`course_id`),
  CONSTRAINT `fk_course_subjects_course` FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE IF NOT EXISTS `education_contents` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `active` bit(1) NOT NULL,
  `article_content` text NOT NULL,
  `category` varchar(255) DEFAULT NULL,
  `content_type` varchar(255) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `faqs` text,
  `meta_description` text NOT NULL,
  `meta_title` varchar(255) NOT NULL,
  `primary_keyword` varchar(255) NOT NULL,
  `secondary_keywords` text,
  `seo_title` varchar(255) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `url_slug` varchar(255) NOT NULL,
  `country_id` bigint DEFAULT NULL,
  `course_id` bigint DEFAULT NULL,
  `university_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_education_contents_url_slug` (`url_slug`),
  KEY `idx_content_slug` (`url_slug`),
  KEY `idx_content_category` (`category`),
  KEY `idx_edu_contents_country_id` (`country_id`),
  KEY `idx_edu_contents_course_id` (`course_id`),
  KEY `idx_edu_contents_university_id` (`university_id`),
  CONSTRAINT `fk_education_contents_country` FOREIGN KEY (`country_id`) REFERENCES `countries` (`id`),
  CONSTRAINT `fk_education_contents_course` FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`),
  CONSTRAINT `fk_education_contents_university` FOREIGN KEY (`university_id`) REFERENCES `universities` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE IF NOT EXISTS `exam_countries` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `is_active` bit(1) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `country_id` bigint NOT NULL,
  `exam_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_exam_countries_exam_country` (`exam_id`,`country_id`),
  KEY `idx_exam_countries_country_id` (`country_id`),
  CONSTRAINT `fk_exam_countries_country` FOREIGN KEY (`country_id`) REFERENCES `countries` (`id`),
  CONSTRAINT `fk_exam_countries_exam` FOREIGN KEY (`exam_id`) REFERENCES `exams` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE IF NOT EXISTS `exam_requirements` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `is_required` bit(1) NOT NULL,
  `minimum_percentile` double DEFAULT NULL,
  `minimum_score` double DEFAULT NULL,
  `updated_at` datetime(6) NOT NULL,
  `college_id` bigint DEFAULT NULL,
  `country_id` bigint NOT NULL,
  `course_id` bigint DEFAULT NULL,
  `exam_id` bigint NOT NULL,
  `university_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_exam_req_college_id` (`college_id`),
  KEY `idx_exam_req_country_id` (`country_id`),
  KEY `idx_exam_req_course_id` (`course_id`),
  KEY `idx_exam_req_exam_id` (`exam_id`),
  KEY `idx_exam_req_university_id` (`university_id`),
  CONSTRAINT `fk_exam_requirements_college` FOREIGN KEY (`college_id`) REFERENCES `colleges` (`id`),
  CONSTRAINT `fk_exam_requirements_country` FOREIGN KEY (`country_id`) REFERENCES `countries` (`id`),
  CONSTRAINT `fk_exam_requirements_course` FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`),
  CONSTRAINT `fk_exam_requirements_exam` FOREIGN KEY (`exam_id`) REFERENCES `exams` (`id`),
  CONSTRAINT `fk_exam_requirements_university` FOREIGN KEY (`university_id`) REFERENCES `universities` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE IF NOT EXISTS `leads` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `admin_notes` text,
  `assigned_counselor_email` varchar(255) DEFAULT NULL,
  `city` varchar(255) DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `email` varchar(255) NOT NULL,
  `message` text,
  `name` varchar(255) NOT NULL,
  `phone` varchar(255) NOT NULL,
  `preferred_budget_usd` double DEFAULT NULL,
  `source` varchar(255) DEFAULT NULL,
  `status` enum('CLOSED','CONTACTED','CONVERTED','IN_PROGRESS','NEW') NOT NULL,
  `target_intake` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) NOT NULL,
  `utm_campaign` varchar(255) DEFAULT NULL,
  `utm_medium` varchar(255) DEFAULT NULL,
  `utm_source` varchar(255) DEFAULT NULL,
  `college_id` bigint DEFAULT NULL,
  `country_id` bigint DEFAULT NULL,
  `course_id` bigint DEFAULT NULL,
  `university_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_lead_email_phone` (`email`,`phone`),
  KEY `idx_lead_status` (`status`),
  KEY `idx_leads_college_id` (`college_id`),
  KEY `idx_leads_country_id` (`country_id`),
  KEY `idx_leads_course_id` (`course_id`),
  KEY `idx_leads_university_id` (`university_id`),
  CONSTRAINT `fk_leads_college` FOREIGN KEY (`college_id`) REFERENCES `colleges` (`id`),
  CONSTRAINT `fk_leads_country` FOREIGN KEY (`country_id`) REFERENCES `countries` (`id`),
  CONSTRAINT `fk_leads_course` FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`),
  CONSTRAINT `fk_leads_university` FOREIGN KEY (`university_id`) REFERENCES `universities` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE IF NOT EXISTS `university_accreditations` (
  `university_id` bigint NOT NULL,
  `accreditation` varchar(255) DEFAULT NULL,
  KEY `idx_univ_accreditations_univ_id` (`university_id`),
  CONSTRAINT `fk_university_accreditations_university` FOREIGN KEY (`university_id`) REFERENCES `universities` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE IF NOT EXISTS `university_available_intakes` (
  `university_id` bigint NOT NULL,
  `intake` varchar(255) DEFAULT NULL,
  KEY `idx_univ_avail_intakes_univ_id` (`university_id`),
  CONSTRAINT `fk_univ_avail_intakes_university` FOREIGN KEY (`university_id`) REFERENCES `universities` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE IF NOT EXISTS `university_course_mapping` (
  `university_id` bigint NOT NULL,
  `course_id` bigint NOT NULL,
  KEY `idx_univ_course_map_course_id` (`course_id`),
  KEY `idx_univ_course_map_univ_id` (`university_id`),
  CONSTRAINT `fk_university_course_mapping_course` FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`),
  CONSTRAINT `fk_university_course_mapping_university` FOREIGN KEY (`university_id`) REFERENCES `universities` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE IF NOT EXISTS `university_facilities` (
  `university_id` bigint NOT NULL,
  `facility` varchar(255) DEFAULT NULL,
  KEY `idx_univ_facilities_univ_id` (`university_id`),
  CONSTRAINT `fk_university_facilities_university` FOREIGN KEY (`university_id`) REFERENCES `universities` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE IF NOT EXISTS `wishlists` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `student_email` varchar(255) NOT NULL,
  `college_id` bigint DEFAULT NULL,
  `course_id` bigint DEFAULT NULL,
  `university_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_wishlists_email_univ_course` (`student_email`,`university_id`,`course_id`),
  KEY `idx_wishlists_college_id` (`college_id`),
  KEY `idx_wishlists_course_id` (`course_id`),
  KEY `idx_wishlists_university_id` (`university_id`),
  CONSTRAINT `fk_wishlists_college` FOREIGN KEY (`college_id`) REFERENCES `colleges` (`id`),
  CONSTRAINT `fk_wishlists_course` FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`),
  CONSTRAINT `fk_wishlists_university` FOREIGN KEY (`university_id`) REFERENCES `universities` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;