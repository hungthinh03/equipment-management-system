--
-- PostgreSQL database dump
--

-- Dumped from database version 17.5
-- Dumped by pg_dump version 17.5

-- Started on 2025-10-23 10:37:07

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
-- TOC entry 221 (class 1255 OID 16646)
-- Name: set_updated_at(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.set_updated_at() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
  NEW.updated_at = now();
  RETURN NEW;
END;
$$;


ALTER FUNCTION public.set_updated_at() OWNER TO postgres;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 220 (class 1259 OID 16664)
-- Name: registries; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.registries (
    id integer NOT NULL,
    request_id integer NOT NULL,
    name character varying(100) NOT NULL,
    type character varying(50) NOT NULL,
    serial_number character varying(100) NOT NULL,
    manufacturer character varying(100) NOT NULL
);


ALTER TABLE public.registries OWNER TO postgres;

--
-- TOC entry 219 (class 1259 OID 16663)
-- Name: registries_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.registries_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.registries_id_seq OWNER TO postgres;

--
-- TOC entry 4815 (class 0 OID 0)
-- Dependencies: 219
-- Name: registries_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.registries_id_seq OWNED BY public.registries.id;


--
-- TOC entry 218 (class 1259 OID 16609)
-- Name: requests; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.requests (
    id integer NOT NULL,
    request_type character varying(20) DEFAULT 'ASSIGN'::character varying NOT NULL,
    device_uuid uuid,
    requester_id integer NOT NULL,
    reason text,
    status character varying(20) DEFAULT 'PENDING'::character varying NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    processed_by_manager integer,
    manager_processed_at timestamp with time zone,
    manager_comment text,
    processed_by_it integer,
    it_processed_at timestamp with time zone,
    it_comment text,
    delivered_by integer,
    delivered_at timestamp with time zone,
    release_submitted_at timestamp with time zone,
    closed_by integer,
    CONSTRAINT chk_request_type CHECK (((request_type)::text = ANY ((ARRAY['ASSIGN'::character varying, 'REGISTER'::character varying])::text[]))),
    CONSTRAINT status_check CHECK (((status)::text = ANY (ARRAY[('PENDING'::character varying)::text, ('APPROVED'::character varying)::text, ('DELIVERED'::character varying)::text, ('REJECTED'::character varying)::text, ('CLOSED'::character varying)::text, ('CANCELLED'::character varying)::text])))
);


ALTER TABLE public.requests OWNER TO postgres;

--
-- TOC entry 217 (class 1259 OID 16608)
-- Name: requests_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.requests_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.requests_id_seq OWNER TO postgres;

--
-- TOC entry 4816 (class 0 OID 0)
-- Dependencies: 217
-- Name: requests_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.requests_id_seq OWNED BY public.requests.id;


--
-- TOC entry 4652 (class 2604 OID 16667)
-- Name: registries id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.registries ALTER COLUMN id SET DEFAULT nextval('public.registries_id_seq'::regclass);


--
-- TOC entry 4647 (class 2604 OID 16612)
-- Name: requests id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.requests ALTER COLUMN id SET DEFAULT nextval('public.requests_id_seq'::regclass);


--
-- TOC entry 4809 (class 0 OID 16664)
-- Dependencies: 220
-- Data for Name: registries; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.registries (id, request_id, name, type, serial_number, manufacturer) FROM stdin;
\.


--
-- TOC entry 4807 (class 0 OID 16609)
-- Dependencies: 218
-- Data for Name: requests; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.requests (id, device_uuid, requester_id, reason, status, created_at, updated_at, processed_by_manager, manager_processed_at, processed_by_it, it_processed_at, manager_comment, it_comment, release_submitted_at, closed_by, delivered_by, delivered_at, request_type) FROM stdin;
\.


--
-- TOC entry 4817 (class 0 OID 0)
-- Dependencies: 219
-- Name: registries_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.registries_id_seq', 1, false);


--
-- TOC entry 4818 (class 0 OID 0)
-- Dependencies: 217
-- Name: requests_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.requests_id_seq', 1, false);


--
-- TOC entry 4658 (class 2606 OID 16669)
-- Name: registries registries_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.registries
    ADD CONSTRAINT registries_pkey PRIMARY KEY (id);


--
-- TOC entry 4656 (class 2606 OID 16619)
-- Name: requests requests_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.requests
    ADD CONSTRAINT requests_pkey PRIMARY KEY (id);


--
-- TOC entry 4660 (class 2620 OID 16647)
-- Name: requests trigger_set_updated_at; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER trigger_set_updated_at BEFORE UPDATE ON public.requests FOR EACH ROW EXECUTE FUNCTION public.set_updated_at();


--
-- TOC entry 4659 (class 2606 OID 16670)
-- Name: registries registries_request_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.registries
    ADD CONSTRAINT registries_request_id_fkey FOREIGN KEY (request_id) REFERENCES public.requests(id) ON DELETE CASCADE;


-- Completed on 2025-10-23 10:37:07

--
-- PostgreSQL database dump complete
--

