// Imports
import { useState } from "react";
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

YupPassword(Yup);

// Put get specializations here (use useEffect)
const specializations = [
	{
		label: "Computer",
		value: 1,
	},
	{
		label: "Mobile",
		value: 2,
	},
];

const CreateTicketForm = () => {
	const [loading, setLoading] = useState(false);

	const notify = useNotification();
	const navigate = useNavigate();

	const CreateTicketSchema = Yup.object().shape({
		title: Yup.string().required("Title is mandatory"),
		description: Yup.string().required("Description is mandatory"),
		productEAN: Yup.string().required("Product EAN is mandatory"),
		specialization: Yup.number()
			.oneOf(specializations.map(spec => spec.value), "Specialization should be a selectable ones")
			.integer()
			.required("Specialization is mandatory"),
	});

	const handleSubmit = (values) => {
		const { title, description, productEAN, specialization } = values;
		setLoading(true);
        api.ticket.createTicket(title, description, productEAN, parseInt(specialization))
        .then((ticket) => {
            notify.success("Ticket successfully created")
            navigate(`/tickets/${ticket.id}`, {replace: true, state: { ticket }})
        })
        .catch((err) => notify.error(err.detail ?? err))
        .finally(() => setLoading(false));
	};

	return (
		<Formik
			initialValues={{
				title: "",
				description: "",
				productEAN: "",
				specialization: -1,
			}}
			validationSchema={CreateTicketSchema}
			onSubmit={(values) => handleSubmit(values)}
		>
			{({ touched, isValid }) => {
				const disableSubmit =
					(!touched.title &&
						!touched.description &&
						!touched.productEAN &&
						!touched.specialization) ||
					!isValid ||
					loading;
				return (
					<Form>
						{fields.createTicket.map((props) => {
							return (
								<Col key={`input-${props.id}`} xs={12}>
									<InputField
										{...props}
                                        options={specializations}
									/>
								</Col>
							);
						})}
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
							Create
						</Button>
					</Form>
				);
			}}
		</Formik>
	);
};

export default CreateTicketForm;
