--
-- PostgreSQL database dump
--

-- Dumped from database version 17.5
-- Dumped by pg_dump version 17.5

-- Started on 2025-10-23 10:22:22

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
-- TOC entry 223 (class 1255 OID 16651)
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
-- TOC entry 222 (class 1259 OID 16584)
-- Name: device_categories; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.device_categories (
    id integer NOT NULL,
    name character varying(20) NOT NULL,
    managed_by character varying(20) NOT NULL
);


ALTER TABLE public.device_categories OWNER TO postgres;

--
-- TOC entry 221 (class 1259 OID 16583)
-- Name: device_categories_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.device_categories_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.device_categories_id_seq OWNER TO postgres;

--
-- TOC entry 4833 (class 0 OID 0)
-- Dependencies: 221
-- Name: device_categories_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.device_categories_id_seq OWNED BY public.device_categories.id;


--
-- TOC entry 218 (class 1259 OID 16560)
-- Name: device_types; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.device_types (
    id integer NOT NULL,
    name character varying(50) NOT NULL,
    category_id integer NOT NULL
);


ALTER TABLE public.device_types OWNER TO postgres;

--
-- TOC entry 217 (class 1259 OID 16559)
-- Name: device_types_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.device_types_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.device_types_id_seq OWNER TO postgres;

--
-- TOC entry 4834 (class 0 OID 0)
-- Dependencies: 217
-- Name: device_types_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.device_types_id_seq OWNED BY public.device_types.id;


--
-- TOC entry 220 (class 1259 OID 16569)
-- Name: devices; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.devices (
    id integer NOT NULL,
    uuid uuid DEFAULT gen_random_uuid(),
    name character varying(100) NOT NULL,
    type_id integer NOT NULL,
    status character varying(20) DEFAULT 'AVAILABLE'::character varying NOT NULL,
    assigned_to integer,
    ownership_type character varying(20) DEFAULT 'COMPANY'::character varying NOT NULL,
    owned_by integer,
    purchase_price numeric(12,2),
    purchase_date timestamp with time zone,
    serial_number character varying(50) NOT NULL,
    manufacturer character varying(100) NOT NULL,
    created_by integer NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_by integer NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    retired_at timestamp with time zone,
    CONSTRAINT chk_ownership_type CHECK (((ownership_type)::text = ANY ((ARRAY['COMPANY'::character varying, 'BYOD'::character varying, 'LEASED'::character varying])::text[])))
);


ALTER TABLE public.devices OWNER TO postgres;

--
-- TOC entry 219 (class 1259 OID 16568)
-- Name: devices_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.devices_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.devices_id_seq OWNER TO postgres;

--
-- TOC entry 4835 (class 0 OID 0)
-- Dependencies: 219
-- Name: devices_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.devices_id_seq OWNED BY public.devices.id;


--
-- TOC entry 4659 (class 2604 OID 16587)
-- Name: device_categories id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.device_categories ALTER COLUMN id SET DEFAULT nextval('public.device_categories_id_seq'::regclass);


--
-- TOC entry 4652 (class 2604 OID 16563)
-- Name: device_types id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.device_types ALTER COLUMN id SET DEFAULT nextval('public.device_types_id_seq'::regclass);


--
-- TOC entry 4653 (class 2604 OID 16572)
-- Name: devices id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.devices ALTER COLUMN id SET DEFAULT nextval('public.devices_id_seq'::regclass);


--
-- TOC entry 4827 (class 0 OID 16584)
-- Dependencies: 222
-- Data for Name: device_categories; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.device_categories (id, name, managed_by) FROM stdin;
1	GENERAL	ADMIN
2	NETWORK	IT
\.


--
-- TOC entry 4823 (class 0 OID 16560)
-- Dependencies: 218
-- Data for Name: device_types; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.device_types (id, name, category_id) FROM stdin;
1	Laptop	1
2	Desktop	1
3	Monitor	1
4	Keyboard	1
5	Switch	2
6	Firewall	2
7	Router	2
\.


--
-- TOC entry 4825 (class 0 OID 16569)
-- Dependencies: 220
-- Data for Name: devices; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.devices (id, name, type_id, status, assigned_to, uuid, created_at, updated_at, serial_number, manufacturer, purchase_price, purchase_date, retired_at, ownership_type, owned_by, created_by, updated_by) FROM stdin;
1	Lenovo ThinkPad X1	1	AVAILABLE	\N	1a1524df-0872-465b-89f8-9f711eb981cc	2025-09-23 10:24:36.304667+07	2025-10-17 16:03:40.419834+07	SN-LEN-1001	Lenovo	1700.00	2020-06-25 00:00:00+07	\N	COMPANY	\N	1	2
2	Dell XPS 15	1	AVAILABLE	\N	b436a16d-4906-45a7-88a9-6d14d2148f2e	2025-09-23 10:24:36.304667+07	2025-10-23 10:19:20.724718+07	SN-DEL-1001	Dell	1800.00	2023-08-15 00:00:00+07	\N	COMPANY	\N	1	1
3	Dell OptiPlex 3080	2	AVAILABLE	\N	b7874077-5d11-4214-8a2e-cb541d384196	2025-09-23 10:24:36.304667+07	2025-10-23 10:19:20.724718+07	SN-DEL-2001	Dell	1200.00	2022-05-10 00:00:00+07	\N	COMPANY	\N	1	1
4	HP ProDesk 400	2	AVAILABLE	\N	d3a6d870-b6f8-4066-8dc1-d5913fd674dd	2025-09-23 10:24:36.304667+07	2025-10-23 10:19:20.724718+07	SN-HP-2101	HP	950.00	2021-11-18 00:00:00+07	\N	COMPANY	\N	1	1
5	HP Z27	3	AVAILABLE	\N	d1b0400d-6aa0-44f8-8f90-85967b340030	2025-09-23 10:24:36.304667+07	2025-10-23 10:19:20.724718+07	SN-HP-3001	HP	450.00	2020-07-12 00:00:00+07	\N	COMPANY	\N	1	1
6	Dell U2720Q	3	AVAILABLE	\N	4b17c2cd-4f8c-4ad8-bd03-8172c879d16f	2025-09-23 10:24:36.304667+07	2025-10-23 10:19:20.724718+07	SN-DEL-3002	Dell	500.00	2023-02-03 00:00:00+07	\N	COMPANY	\N	1	1
7	Logitech K380	4	AVAILABLE	\N	cb55663f-54b9-43ff-b837-1c72d0fb7fce	2025-09-23 10:24:36.304667+07	2025-10-23 10:19:20.724718+07	SN-LOG-4001	Logitech	40.00	2024-01-20 00:00:00+07	\N	COMPANY	\N	1	1
8	Apple Magic Keyboard	4	AVAILABLE	\N	afeb4d80-8e1e-4246-b7ca-1aab78f9e4c8	2025-09-23 10:24:36.304667+07	2025-10-23 10:19:20.724718+07	SN-APL-4002	Apple	100.00	2023-09-05 00:00:00+07	\N	COMPANY	\N	1	1
9	Cisco Switch 3750	5	AVAILABLE	\N	c2950cc0-afa2-44e1-80a1-44603bc68b4e	2025-09-23 10:24:36.304667+07	2025-10-23 10:19:20.724718+07	SN-CIS-5002	Cisco	2800.00	2019-10-10 00:00:00+07	\N	COMPANY	\N	5	5
10	Cisco Switch 2960	5	AVAILABLE	\N	077d52ef-7796-423f-a326-e7e92ca54320	2025-09-23 10:24:36.304667+07	2025-10-23 10:19:20.724718+07	SN-CIS-5001	Cisco	2500.00	2019-12-11 00:00:00+07	\N	COMPANY	\N	5	5
11	Cisco ASA 5506	6	AVAILABLE	\N	2bf7efd3-97f3-40ca-b052-627ae0f18f82	2025-09-23 10:24:36.304667+07	2025-10-23 10:19:20.724718+07	SN-CIS-6001	Cisco	1800.00	2022-03-14 00:00:00+07	\N	COMPANY	\N	5	5
12	Fortinet Firewall	6	AVAILABLE	\N	7f03115c-45d2-46ae-9b95-3feba5678aa9	2025-09-23 10:24:36.304667+07	2025-10-23 10:19:20.724718+07	SN-FOR-6002	Fortinet	2100.00	2023-06-01 00:00:00+07	\N	COMPANY	\N	5	5
13	MikroTik hAP ac	7	AVAILABLE	\N	0727b5a4-53ff-41fc-964f-d97666a303f3	2025-09-23 10:24:36.304667+07	2025-10-23 10:19:20.724718+07	SN-MIK-7002	MikroTik	120.00	2023-01-29 00:00:00+07	\N	COMPANY	\N	5	5
14	Netgear Router	7	AVAILABLE	\N	5c445ad8-7871-4773-962d-2da2744b7e7e	2025-09-23 10:24:36.304667+07	2025-10-23 10:19:20.724718+07	SN-NET-7001	Netgear	350.00	2024-03-07 00:00:00+07	\N	COMPANY	\N	5	5
\.


--
-- TOC entry 4836 (class 0 OID 0)
-- Dependencies: 221
-- Name: device_categories_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.device_categories_id_seq', 1, false);


--
-- TOC entry 4837 (class 0 OID 0)
-- Dependencies: 217
-- Name: device_types_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.device_types_id_seq', 7, true);


--
-- TOC entry 4838 (class 0 OID 0)
-- Dependencies: 219
-- Name: devices_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.devices_id_seq', 14, true);


--
-- TOC entry 4661 (class 2606 OID 16682)
-- Name: devices chk_status; Type: CHECK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE public.devices
    ADD CONSTRAINT chk_status CHECK (((status)::text = ANY (ARRAY[('AVAILABLE'::character varying)::text, ('RESERVED'::character varying)::text, ('ASSIGNED'::character varying)::text, ('MAINTENANCE'::character varying)::text, ('RETIRED'::character varying)::text]))) NOT VALID;


--
-- TOC entry 4673 (class 2606 OID 16589)
-- Name: device_categories device_categories_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.device_categories
    ADD CONSTRAINT device_categories_pkey PRIMARY KEY (id);


--
-- TOC entry 4663 (class 2606 OID 16567)
-- Name: device_types device_types_name_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.device_types
    ADD CONSTRAINT device_types_name_key UNIQUE (name);


--
-- TOC entry 4665 (class 2606 OID 16565)
-- Name: device_types device_types_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.device_types
    ADD CONSTRAINT device_types_pkey PRIMARY KEY (id);


--
-- TOC entry 4667 (class 2606 OID 16575)
-- Name: devices devices_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.devices
    ADD CONSTRAINT devices_pkey PRIMARY KEY (id);


--
-- TOC entry 4669 (class 2606 OID 16606)
-- Name: devices devices_uuid_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.devices
    ADD CONSTRAINT devices_uuid_key UNIQUE (uuid);


--
-- TOC entry 4671 (class 2606 OID 16661)
-- Name: devices uq_devices_serial; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.devices
    ADD CONSTRAINT uq_devices_serial UNIQUE (serial_number);


--
-- TOC entry 4676 (class 2620 OID 16652)
-- Name: devices trigger_set_updated_at; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER trigger_set_updated_at BEFORE UPDATE ON public.devices FOR EACH ROW EXECUTE FUNCTION public.set_updated_at();


--
-- TOC entry 4675 (class 2606 OID 16576)
-- Name: devices devices_type_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.devices
    ADD CONSTRAINT devices_type_id_fkey FOREIGN KEY (type_id) REFERENCES public.device_types(id);


--
-- TOC entry 4674 (class 2606 OID 16595)
-- Name: device_types fk_device_category; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.device_types
    ADD CONSTRAINT fk_device_category FOREIGN KEY (category_id) REFERENCES public.device_categories(id);


-- Completed on 2025-10-23 10:22:23

--
-- PostgreSQL database dump complete
--

