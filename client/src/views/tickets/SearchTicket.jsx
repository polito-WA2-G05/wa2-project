// Imports
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { Button, Spinner, Col } from "react-bootstrap";
import { Formik, Form } from "formik";
import * as Yup from "yup";

// Components
import { InputField } from "@components/forms";
import { SwitchButton } from "@components";

// Services
import api from "@services";

// Hooks
import { useNotification } from "@hooks";

const SearchTicket = () => {
	const [loading, setLoading] = useState(false);
	const [searchByProduct, setSearchByProduct] = useState(false);

	const navigate = useNavigate();
	const notify = useNotification();

	const SearchTicketSchema = Yup.object().shape({
		ticketId: Yup.number().when({
			is: () => !searchByProduct,
			then: (schema) => schema.required("Ticket Id is mandatory"),
			otherwise: (schema) => schema.notRequired(),
		}),
		productEan: Yup.string().when({
			is: () => searchByProduct,
			then: (schema) => schema.required("Product EAN is mandatory"),
			otherwise: (schema) => schema.notRequired(),
		})
	});

	const handleSubmit = (values) => {
		setLoading(true);
		const { ticketId, productEan } = values;
		const apiHandler = !searchByProduct ? api.ticket.getTicket : api.ticket.getTicketByProductEAN

		apiHandler(!searchByProduct ? ticketId : productEan)
			.then((ticket) => { navigate(`/tickets/${ticket.id}`, { replace: true, state: { ticket } }) })
			.catch((err) => notify.error(err.detail ?? err))
			.finally(() => setLoading(false))
	}

	return (
		<div>
			<h1 className="mb-5 fs-1 fw-bold text-center">Search ticket</h1>
			<Formik
				initialValues={{ ticketId: '', productEan: '' }}
				validationSchema={SearchTicketSchema}
				onSubmit={handleSubmit}
			>
				{({ touched, isValid, resetForm }) => {
					const disableSubmit = (!touched.ticketId && !touched.productEan) || !isValid || loading;

					const handleToggle = () => {
						setSearchByProduct(old => !old)
						resetForm()
					}

					return (
						<Form>
							<div className="d-flex flex-column justify-content-center align-items-center">
								<Col xs={12} lg={3}>
									<InputField
										id={"search-by-product"}
										type={"text"}
										name={!searchByProduct ? "ticketId" : "productEan"}
										placeholder={!searchByProduct ? "Ticket Id" : "Product EAN"}
									/>
									<SwitchButton
										label={"Search By productEAN"}
										onToggle={handleToggle}
									/>
									<Button
										variant="primary"
										type="submit"
										className="py-2 px-5 rounded-3 fw-semibold"
										disabled={disableSubmit}
									>
										{loading && (
											<Spinner
												animation="grow"
												size="sm"
												as="span"
												role="status"
												aria-hidden="true"
												className="me-2"
											/>
										)}
										Search
									</Button>
								</Col>
							</div>
						</Form>
					);
				}}
			</Formik>
		</div>
	);
};

export default SearchTicket;
