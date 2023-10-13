// Imports
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { Spinner, Button, Row, Col } from "react-bootstrap";
import { Formik, Form } from "formik";
import * as Yup from "yup";
import YupPassword from "yup-password";

import fields from "./fields"

// Components
import { MultiSelect, InputField } from "@components/forms";

// Services
import api from "@services";

// Hooks
import { useNotification } from "@hooks";

YupPassword(Yup);

const CreateExpertForm = () => {
	const [loading, setLoading] = useState(false);

	const notify = useNotification();
	const navigate = useNavigate();

	const CreateExpertSchema = Yup.object().shape({
		username: Yup.string().required("Username is mandatory"),
		email: Yup.string().email().required("Email is mandatory"),
		password: Yup.string().password().required("Password is mandatory"),
		confirmPassword: Yup.string()
			.oneOf([Yup.ref("password"), null], "Passwords must match")
			.required(),
		specializations: Yup.array()
			.min(1, "Select at least 1 specialization")
			.of(Yup.number().integer()),
	});

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

	const handleSubmit = (values) => {
		const { username, email, password, specializations } = values;
		setLoading(true);
		api.manager
			.createExpert(username, email, password, specializations)
			.then(() => {
				notify.success("Expert successfully created");
				navigate("/", { replace: true });
			})
			.catch((err) => notify.error(err.detail ?? err))
			.finally(() => setLoading(false));
	};

	return (
		<Formik
			initialValues={{
				username: "",
				email: "",
				password: "",
				confirmPassword: "",
				specializations: [],
			}}
			validationSchema={CreateExpertSchema}
			onSubmit={(values) => handleSubmit(values)}
		>
			{({ touched, isValid }) => {
				const disableSubmit =
					(!touched.username &&
						!touched.email &&
						!touched.password &&
						!touched.confirmPassword &&
						!touched.specializations) ||
					!isValid ||
					loading;
				return (
					<Form>
						<Row>
							{fields.createExpert.map((props, idx) => {
								return (
									<Col key={`input-${props.id}`} xs={12} sm={6}>
										<InputField
											id={props.id}
											type={props.type}
											name={props.name}
											placeholder={props.placeholder}
										/>
									</Col>
								);
							})}
						</Row>
						<Row>
							<MultiSelect
								name={"specializations"}
								options={specializations}
								placeholder={"Select spacializations..."}
							/>
						</Row>
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

export default CreateExpertForm;
