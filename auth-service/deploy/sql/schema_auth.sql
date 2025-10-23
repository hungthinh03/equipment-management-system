--
-- PostgreSQL database dump
--

-- Dumped from database version 17.5
-- Dumped by pg_dump version 17.5

-- Started on 2025-10-23 10:13:31

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 850 (class 1247 OID 16525)
-- Name: users_role; Type: TYPE; Schema: public; Owner: postgres
--

CREATE TYPE public.users_role AS ENUM (
    'ADMIN',
    'EMPLOYEE',
    'IT'
);


ALTER TYPE public.users_role OWNER TO postgres;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 217 (class 1259 OID 16472)
-- Name: users; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.users (
    id integer NOT NULL,
    email character varying(100) NOT NULL,
    password character varying(255) NOT NULL,
    role character varying(20) NOT NULL
);


ALTER TABLE public.users OWNER TO postgres;

--
-- TOC entry 218 (class 1259 OID 16475)
-- Name: users _id _seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public."users _id _seq"
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public."users _id _seq" OWNER TO postgres;

--
-- TOC entry 4800 (class 0 OID 0)
-- Dependencies: 218
-- Name: users _id _seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public."users _id _seq" OWNED BY public.users.id;


--
-- TOC entry 4644 (class 2604 OID 16476)
-- Name: users id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users ALTER COLUMN id SET DEFAULT nextval('public."users _id _seq"'::regclass);


--
-- TOC entry 4793 (class 0 OID 16472)
-- Dependencies: 217
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.users (id, email, password, role) FROM stdin;
1	admin1@example.com	$2a$10$fS08JEyuZE7TxiaMUmUBSuxXa9/awxLCZtRF5CRk9MtexHMJjFwcy	ADMIN
2	admin2@example.com	$2a$10$fS08JEyuZE7TxiaMUmUBSuxXa9/awxLCZtRF5CRk9MtexHMJjFwcy	ADMIN
3	employee1@example.com	$2a$10$fS08JEyuZE7TxiaMUmUBSuxXa9/awxLCZtRF5CRk9MtexHMJjFwcy	EMPLOYEE
4	employee2@example.com	$2a$10$fS08JEyuZE7TxiaMUmUBSuxXa9/awxLCZtRF5CRk9MtexHMJjFwcy	EMPLOYEE
5	it1@example.com	$2a$10$fS08JEyuZE7TxiaMUmUBSuxXa9/awxLCZtRF5CRk9MtexHMJjFwcy	IT
6	it2@example.com	$2a$10$fS08JEyuZE7TxiaMUmUBSuxXa9/awxLCZtRF5CRk9MtexHMJjFwcy	IT
\.


--
-- TOC entry 4801 (class 0 OID 0)
-- Dependencies: 218
-- Name: users _id _seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public."users _id _seq"', 6, true);


--
-- TOC entry 4645 (class 2606 OID 16553)
-- Name: users role_check; Type: CHECK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE public.users
    ADD CONSTRAINT role_check CHECK (((role)::text = ANY ((ARRAY['ADMIN'::character varying, 'EMPLOYEE'::character varying, 'IT'::character varying])::text[]))) NOT VALID;


--
-- TOC entry 4647 (class 2606 OID 16481)
-- Name: users users _pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT "users _pkey" PRIMARY KEY (id);


-- Completed on 2025-10-23 10:13:31

--
-- PostgreSQL database dump complete
--

