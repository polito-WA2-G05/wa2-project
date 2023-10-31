// Imports
import { useState, useEffect, useContext } from "react";
import { useNavigate } from "react-router-dom";
import { Spinner, Button, Col } from "react-bootstrap";
import { Formik, Form } from "formik";
import * as Yup from "yup";
import YupPassword from "yup-password";

import fields from "./fields";

// Components
import { InputField } from "@components/forms";

// Services
import api from "@services";

// Hooks 	
import { useNotification } from "@hooks";
import { SessionContext } from "@contexts";

YupPassword(Yup);

const CreateTicketForm = ({ ean }) => {
	const [loading, setLoading] = useState(false);
	const [loadingSpecializations, setLoadingSpecializations] = useState(false);
	const [specializations, setSpecializations] = useState([])
	
	const { onError } = useContext(SessionContext)

	const notify = useNotification();
	const navigate = useNavigate();

	const CreateTicketSchema = Yup.object().shape({
		title: Yup.string().required("Title is mandatory"),
		description: Yup.string().required("Description is mandatory"),
		specialization: Yup.number()
			.oneOf(specializations.map(spec => spec.id), "Specialization should be a selectable ones")
			.integer()
			.required("Specialization is mandatory"),
	});

	useEffect(() => {
		setLoadingSpecializations(true);
		api.ticket.getSpecializations()
			.then((specialization) => setSpecializations(specialization))
			.catch(onError)
			.finally(() => setLoadingSpecializations(false))
	},[]) // eslint-disable-line
	
	const handleSubmit = (values) => {
		const { title, description, specialization } = values;

		setLoading(true);
        api.ticket.createTicket(title, description, ean, parseInt(specialization))
        .then((ticket) => {
            notify.success("Ticket successfully created")
            navigate(`/tickets/${ticket.id}`, {replace: true, state: { ticket }})
        })
        .catch(onError)
        .finally(() => setLoading(false));
	};

	return (
		<Formik
			initialValues={{
				title: "",
				description: "",
				specialization: -1,
			}}
			validationSchema={CreateTicketSchema}
			onSubmit={handleSubmit}
		>
			{({ touched, isValid }) => {
				const disableSubmit =
					(!touched.title &&
						!touched.description &&
						!touched.specialization) ||
					!isValid ||
					loading || loadingSpecializations;
				return (
					<Form>
						{fields.createTicket.map((props) => {
							return (
								<Col key={`input-${props.name}`} xs={12}>
									<InputField
										{...props}
                                        options={specializations}
									/>
								</Col>
							);
						})}
						<div className="d-flex align-items-center justify-content-between">
							<Button
								variant="secondary"
								className="p-2 rounded-3 my-4 me-5 w-100 fw-semibold"
								onClick={() => navigate(-1, { replace: true })}
								>
								Go Back
							</Button>
							<Button
								variant="primary"
								type="submit"
								className="p-2 rounded-3 my-4 w-100 fw-semibold"
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
								Submit
							</Button>
						</div>
					</Form>
				);
			}}
		</Formik>
	);
};

export default CreateTicketForm;
