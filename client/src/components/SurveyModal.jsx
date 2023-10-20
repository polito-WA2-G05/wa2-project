// Imports
import React, { useState } from "react";
import { Modal, Button, Row, Col, Spinner } from "react-bootstrap";
import { useNavigate } from "react-router-dom";
import { Formik, Form } from "formik";
import * as Yup from "yup";
import YupPassword from "yup-password";

import fields from "@components/forms/fields";

// Styles
import { BsSend } from "react-icons/bs";

// Components
import { InputField } from "@components/forms";

// Services
import api from "@services";

// Hooks
import { useNotification } from "@hooks";

// Utils
import { Rating } from "@utils";

YupPassword(Yup);

const SurveyModal = ({ show, onHide, onConfirm, ticketId }) => {
	const [loading, setLoading] = useState(false);
	const notify = useNotification();
	const navigate = useNavigate();

	const handleClose = () => {
		onHide();
	};

	const SurveySchema = Yup.object().shape({
		serviceValuation: Yup.number()
			.min(0)
			.oneOf(Object.values(Rating), "Invalid rating value")
			.integer()
			.required("Rating is mandatory"),
		professionality: Yup.number()
			.min(0)
			.integer()
			.oneOf(Object.values(Rating), "Invalid rating value")
			.required("Rating is mandatory"),
		comment: Yup.string(),
	});

	const handleSubmit = (values) => {
		const { serviceValuation, professionality, comment } = values;
		setLoading(true);

		api.ticket
			.sendSurvey(ticketId, serviceValuation, professionality, comment)
			.then((ticket) => {
				onConfirm(ticket)
				notify.success("Survey successfully sent");
				navigate("/tickets");
			})
			.catch((err) => notify.error(err.detail ?? err))
			.finally(() => setLoading(false));

		handleClose();
	};

	return (
		<Modal show={show} onHide={handleClose} centered>
			<Modal.Header closeButton>
				<Modal.Title>Leave a feedback</Modal.Title>
			</Modal.Header>
			<Modal.Body>
				<Formik
					initialValues={{
						serviceValuation: -1,
						professionality: -1,
						comment: "",
					}}
					validationSchema={SurveySchema}
					onSubmit={handleSubmit}
				>
					{({ touched, isValid }) => {
						const disableSubmit =
							(!touched.serviceValuation &&
								!touched.professionality &&
								!touched.comment) ||
							!isValid ||
							loading;
						return (
							<Form>
								{fields.surveyModal.map((props) => {
									return (
										<InputField key={`input-${props.id}`} {...props} />
									);
								})}
								<div className="d-flex justify-content-between">
									<Button
										variant="secondary"
										onClick={handleClose}
										className="p-2 rounded-3 my-2 me-4 fw-semibold w-100"
									>
										Cancel
									</Button>
									<Button
										variant="primary"
										type="submit"
										className="p-2 rounded-3 my-2 ms-4 fw-semibold w-100"
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
										<BsSend size={15} className="me-2" />
										Submit
									</Button>
								</div>
							</Form>
						);
					}}
				</Formik>
			</Modal.Body>
		</Modal>
	);
};

export default SurveyModal;
