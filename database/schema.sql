-- =====================================================================================
-- CSIT314 Group Project - Interior Design Platform (IDP)
-- Relational schema, transcribed from the final ERD diagram.
--
-- Eight tables, one per entity in the final class diagram. The Java model keeps the same
-- names and the same columns, so a row of these tables maps directly onto an entity:
--
--   USER_ACCOUNT     -> idp.entity.UserAccount
--   USER_PROFILE     -> idp.entity.UserProfile
--   COMPANY_PROFILE  -> idp.entity.CompanyProfile
--   DESIGN_PROJECT   -> idp.entity.DesignProject
--   PROJECT_IMAGE    -> idp.entity.ProjectImage
--   PROJECT_CATEGORY -> idp.entity.ProjectCategory
--   FAVORITE         -> idp.entity.Favorite
--   ACTIVITY_EVENT   -> idp.entity.ActivityEvent
--
-- The enumerations of the class diagram are stored as VARCHAR and guarded by CHECK
-- constraints, so the database rejects a role or a status the Java enums do not know.
--
-- Written in portable SQL; the CHECK constraints need MySQL 8.0.16+ or PostgreSQL 12+.
-- =====================================================================================

DROP TABLE IF EXISTS ACTIVITY_EVENT;
DROP TABLE IF EXISTS FAVORITE;
DROP TABLE IF EXISTS PROJECT_IMAGE;
DROP TABLE IF EXISTS DESIGN_PROJECT;
DROP TABLE IF EXISTS PROJECT_CATEGORY;
DROP TABLE IF EXISTS COMPANY_PROFILE;
DROP TABLE IF EXISTS USER_PROFILE;
DROP TABLE IF EXISTS USER_ACCOUNT;

-- -------------------------------------------------------------------------------------
-- USER_ACCOUNT - one login identity per person, for all four roles.
-- -------------------------------------------------------------------------------------
CREATE TABLE USER_ACCOUNT (
    account_id    INT           NOT NULL AUTO_INCREMENT,
    username      VARCHAR(50)   NOT NULL,
    email         VARCHAR(255)  NOT NULL,
    password_hash VARCHAR(255)  NOT NULL,
    role          VARCHAR(30)   NOT NULL,
    status        VARCHAR(20)   NOT NULL DEFAULT 'ACTIVE',
    created_at    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_user_account        PRIMARY KEY (account_id),
    CONSTRAINT uq_user_account_name   UNIQUE (username),
    CONSTRAINT uq_user_account_email  UNIQUE (email),
    CONSTRAINT ck_user_account_role   CHECK (role IN ('USER_ADMIN', 'INTERIOR_DESIGNER',
                                                      'CUSTOMER', 'PLATFORM_MANAGEMENT')),
    CONSTRAINT ck_user_account_status CHECK (status IN ('ACTIVE', 'SUSPENDED'))
);

-- -------------------------------------------------------------------------------------
-- USER_PROFILE - personal details, one profile per account (1..1 on the class diagram).
-- -------------------------------------------------------------------------------------
CREATE TABLE USER_PROFILE (
    profile_id INT          NOT NULL AUTO_INCREMENT,
    account_id INT          NOT NULL,
    full_name  VARCHAR(100) NOT NULL,
    phone      VARCHAR(30),
    address    VARCHAR(255),
    status     VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_user_profile        PRIMARY KEY (profile_id),
    CONSTRAINT uq_user_profile_account UNIQUE (account_id),
    CONSTRAINT fk_user_profile_account FOREIGN KEY (account_id)
        REFERENCES USER_ACCOUNT (account_id) ON DELETE CASCADE,
    CONSTRAINT ck_user_profile_status  CHECK (status IN ('ACTIVE', 'SUSPENDED'))
);

-- -------------------------------------------------------------------------------------
-- COMPANY_PROFILE - the studio an Interior Designer owns (one company per designer).
-- -------------------------------------------------------------------------------------
CREATE TABLE COMPANY_PROFILE (
    company_id       INT          NOT NULL AUTO_INCREMENT,
    account_id       INT          NOT NULL,
    name             VARCHAR(150) NOT NULL,
    description      TEXT,
    location         VARCHAR(120),
    contact_email    VARCHAR(255),
    phone            VARCHAR(30),
    logo_url         VARCHAR(500),
    status           VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    CONSTRAINT pk_company_profile        PRIMARY KEY (company_id),
    CONSTRAINT uq_company_profile_owner  UNIQUE (account_id),
    CONSTRAINT uq_company_profile_name   UNIQUE (name),
    CONSTRAINT fk_company_profile_owner  FOREIGN KEY (account_id)
        REFERENCES USER_ACCOUNT (account_id) ON DELETE CASCADE,
    CONSTRAINT ck_company_profile_status CHECK (status IN ('ACTIVE', 'INACTIVE'))
);

-- -------------------------------------------------------------------------------------
-- PROJECT_CATEGORY - managed by Platform Management, used to classify projects.
-- -------------------------------------------------------------------------------------
CREATE TABLE PROJECT_CATEGORY (
    category_id INT          NOT NULL AUTO_INCREMENT,
    name        VARCHAR(80)  NOT NULL,
    description VARCHAR(255),
    status      VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    CONSTRAINT pk_project_category        PRIMARY KEY (category_id),
    CONSTRAINT uq_project_category_name   UNIQUE (name),
    CONSTRAINT ck_project_category_status CHECK (status IN ('ACTIVE', 'INACTIVE'))
);

-- -------------------------------------------------------------------------------------
-- DESIGN_PROJECT - the portfolio entry: current work or a finished (past) project.
-- view_count and shortlist_count are the interest figures the designer reports on.
-- -------------------------------------------------------------------------------------
CREATE TABLE DESIGN_PROJECT (
    project_id       INT            NOT NULL AUTO_INCREMENT,
    owner_account_id INT            NOT NULL,
    category_id      INT            NOT NULL,
    title            VARCHAR(150)   NOT NULL,
    description      TEXT,
    style            VARCHAR(80),
    budget           DECIMAL(12, 2),
    stage            VARCHAR(20)    NOT NULL DEFAULT 'CURRENT',
    completion_date  DATE,
    view_count       INT            NOT NULL DEFAULT 0,
    shortlist_count  INT            NOT NULL DEFAULT 0,
    created_at       TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_design_project          PRIMARY KEY (project_id),
    CONSTRAINT fk_design_project_owner    FOREIGN KEY (owner_account_id)
        REFERENCES USER_ACCOUNT (account_id),
    CONSTRAINT fk_design_project_category FOREIGN KEY (category_id)
        REFERENCES PROJECT_CATEGORY (category_id),
    CONSTRAINT ck_design_project_stage    CHECK (stage IN ('CURRENT', 'PAST')),
    CONSTRAINT ck_design_project_budget   CHECK (budget IS NULL OR budget >= 0),
    CONSTRAINT ck_design_project_counts   CHECK (view_count >= 0 AND shortlist_count >= 0),
    -- a past project must have a completion date, a current one must not
    CONSTRAINT ck_design_project_dates    CHECK ((stage = 'PAST' AND completion_date IS NOT NULL)
                                              OR (stage = 'CURRENT' AND completion_date IS NULL))
);

-- -------------------------------------------------------------------------------------
-- PROJECT_IMAGE - the gallery of a project; display_order 0 is the cover image.
-- -------------------------------------------------------------------------------------
CREATE TABLE PROJECT_IMAGE (
    image_id      INT          NOT NULL AUTO_INCREMENT,
    project_id    INT          NOT NULL,
    image_url     VARCHAR(500) NOT NULL,
    display_order INT          NOT NULL DEFAULT 0,
    is_cover      BOOLEAN      NOT NULL DEFAULT FALSE,
    uploaded_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_project_image         PRIMARY KEY (image_id),
    CONSTRAINT fk_project_image_project FOREIGN KEY (project_id)
        REFERENCES DESIGN_PROJECT (project_id) ON DELETE CASCADE,
    CONSTRAINT ck_project_image_order   CHECK (display_order >= 0)
);

-- -------------------------------------------------------------------------------------
-- FAVORITE - the customer's shortlist. One row per (customer, project) pair.
-- -------------------------------------------------------------------------------------
CREATE TABLE FAVORITE (
    favorite_id         INT       NOT NULL AUTO_INCREMENT,
    customer_account_id INT       NOT NULL,
    project_id          INT       NOT NULL,
    saved_at            TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_favorite          PRIMARY KEY (favorite_id),
    CONSTRAINT uq_favorite_pair     UNIQUE (customer_account_id, project_id),
    CONSTRAINT fk_favorite_customer FOREIGN KEY (customer_account_id)
        REFERENCES USER_ACCOUNT (account_id) ON DELETE CASCADE,
    CONSTRAINT fk_favorite_project  FOREIGN KEY (project_id)
        REFERENCES DESIGN_PROJECT (project_id) ON DELETE CASCADE
);

-- -------------------------------------------------------------------------------------
-- ACTIVITY_EVENT - the log the daily, weekly and monthly reports count.
-- -------------------------------------------------------------------------------------
CREATE TABLE ACTIVITY_EVENT (
    event_id         INT         NOT NULL AUTO_INCREMENT,
    actor_account_id INT         NOT NULL,
    project_id       INT         NOT NULL,
    event_type       VARCHAR(40) NOT NULL,
    occurred_at      TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_activity_event         PRIMARY KEY (event_id),
    CONSTRAINT fk_activity_event_actor   FOREIGN KEY (actor_account_id)
        REFERENCES USER_ACCOUNT (account_id) ON DELETE CASCADE,
    CONSTRAINT fk_activity_event_project FOREIGN KEY (project_id)
        REFERENCES DESIGN_PROJECT (project_id) ON DELETE CASCADE,
    CONSTRAINT ck_activity_event_type    CHECK (event_type IN ('PROJECT_VIEW', 'PROJECT_SHORTLIST',
                                                               'PROJECT_CREATE', 'PROJECT_UPDATE',
                                                               'STATUS_CHANGE'))
);

-- -------------------------------------------------------------------------------------
-- Indexes for the searches the use cases need.
-- -------------------------------------------------------------------------------------
CREATE INDEX ix_user_account_role       ON USER_ACCOUNT (role, status);
CREATE INDEX ix_user_profile_name       ON USER_PROFILE (full_name);
CREATE INDEX ix_company_profile_name    ON COMPANY_PROFILE (name);
CREATE INDEX ix_design_project_owner    ON DESIGN_PROJECT (owner_account_id, stage);
CREATE INDEX ix_design_project_category ON DESIGN_PROJECT (category_id, stage);
CREATE INDEX ix_activity_event_period   ON ACTIVITY_EVENT (occurred_at, event_type);
CREATE INDEX ix_favorite_customer       ON FAVORITE (customer_account_id, saved_at);
