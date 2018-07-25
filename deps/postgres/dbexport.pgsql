--
-- PostgreSQL database dump
--
--
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';

--
-- Name: transactions(integer); Type: FUNCTION; Schema: public; Owner: dlebrero
--

CREATE FUNCTION public.transactions(client integer) RETURNS refcursor
    LANGUAGE plpgsql
    AS $$

                     DECLARE

                     ref refcursor;

                     BEGIN

                     OPEN ref FOR SELECT * FROM transaction;

                     RETURN ref;

                     END;

                     $$;


ALTER FUNCTION public.transactions(client integer) OWNER TO dlebrero;

--
-- Name: transactions_in_eur(bigint); Type: FUNCTION; Schema: public; Owner: dlebrero
--

CREATE FUNCTION public.transactions_in_eur(clientid bigint) RETURNS refcursor
    LANGUAGE plpgsql
    AS $$

                     DECLARE

                     ref refcursor;

                     BEGIN

                     OPEN ref FOR SELECT t.id,t.type,t.timestamp,t.amount * e.change as amount FROM transaction t,exchange e where t.client = clientId AND t.currency = e.currency;

                     RETURN ref;

                     END;

                     $$;


ALTER FUNCTION public.transactions_in_eur(clientid bigint) OWNER TO dlebrero;

--
-- Name: transactions_in_gbp(bigint); Type: FUNCTION; Schema: public; Owner: dlebrero
--

CREATE FUNCTION public.transactions_in_gbp(clientid bigint) RETURNS refcursor
    LANGUAGE plpgsql
    AS $$

                     DECLARE

                     ref refcursor;

                     BEGIN

                     OPEN ref FOR SELECT t.id,t.type,t.timestamp,t.amount * e.change as amount FROM transaction t,exchange e where t.client = clientId AND t.currency = e.currency;

                     RETURN ref;

                     END;

                     $$;


ALTER FUNCTION public.transactions_in_gbp(clientid bigint) OWNER TO dlebrero;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: exchange; Type: TABLE; Schema: public; Owner: dlebrero
--

CREATE TABLE public.exchange (
    currency character varying(3),
    change double precision
);


ALTER TABLE public.exchange OWNER TO dlebrero;

--
-- Name: transaction; Type: TABLE; Schema: public; Owner: dlebrero
--

CREATE TABLE public.transaction (
    id integer NOT NULL,
    client integer,
    type integer,
    currency character varying(3),
    amount double precision,
    "timestamp" bigint
);


ALTER TABLE public.transaction OWNER TO dlebrero;

--
-- Name: transaction_id_seq; Type: SEQUENCE; Schema: public; Owner: dlebrero
--

CREATE SEQUENCE public.transaction_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.transaction_id_seq OWNER TO dlebrero;

--
-- Name: transaction_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: dlebrero
--

ALTER SEQUENCE public.transaction_id_seq OWNED BY public.transaction.id;


--
-- Name: transaction id; Type: DEFAULT; Schema: public; Owner: dlebrero
--

ALTER TABLE ONLY public.transaction ALTER COLUMN id SET DEFAULT nextval('public.transaction_id_seq'::regclass);


--
-- Data for Name: exchange; Type: TABLE DATA; Schema: public; Owner: dlebrero
--

COPY public.exchange (currency, change) FROM stdin;
EUR	1.19999999999999996
GBP	1
USD	1.39999999999999991
AUD	1.69999999999999996
\.


--
-- Data for Name: transaction; Type: TABLE DATA; Schema: public; Owner: dlebrero
--

COPY public.transaction (id, client, type, currency, amount, "timestamp") FROM stdin;
1	1	100	GBP	23	1467759398168
2	1	100	GBP	-5	1467759398179
3	1	101	EUR	10	1467759398191
4	1	101	EUR	10	1467759398199
5	2	101	EUR	103.400000000000006	1467759398209
6	2	101	USD	223.400000000000006	1467759398218
7	3	101	AUD	1043.40000000000009	1467759398227
8	4	101	GBP	523.399999999999977	1467759398235
9	5	101	EUR	1503.40000000000009	1467759398245
10	5	101	GBP	3.39999999999999991	1467759398258
11	5	101	EUR	3.39999999999999991	1467759398270
12	6	101	EUR	6.40000000000000036	1467759398281
13	7	101	EUR	453	1467759398293
14	7	101	EUR	22	1467759398301
15	7	101	EUR	1	1467759398310
16	8	101	AUD	17.3000000000000007	1467759398318
17	8	101	AUD	-117.299999999999997	1467759398328
18	8	101	AUD	-1217.29999999999995	1467759398339
19	8	101	AUD	-10.3000000000000007	1467759398348
20	8	101	AUD	-217.300000000000011	1467759398362
21	8	101	USD	34	1467759398380
22	9	101	GBP	23.3999999999999986	1467759398396
23	9	101	USD	100.400000000000006	1467759398417
24	10	101	EUR	20.3999999999999986	1467759398436
25	11	101	EUR	100.400000000000006	1467759398455
26	11	101	EUR	22.3999999999999986	1467759398471
\.


--
-- Name: transaction_id_seq; Type: SEQUENCE SET; Schema: public; Owner: dlebrero
--

SELECT pg_catalog.setval('public.transaction_id_seq', 26, true);


--
-- Name: transaction transaction_pkey; Type: CONSTRAINT; Schema: public; Owner: dlebrero
--

ALTER TABLE ONLY public.transaction
    ADD CONSTRAINT transaction_pkey PRIMARY KEY (id);


--
-- Name: SCHEMA public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- PostgreSQL database dump complete
--

