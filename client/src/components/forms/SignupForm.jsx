// Imports
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { Spinner, Button, Row, Col } from "react-bootstrap";
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

const SignupForm = () => {
	const [loading, setLoading] = useState(false);

	const notify = useNotification();
	const navigate = useNavigate();

	const SignupSchema = Yup.object().shape({
		username: Yup.string().required("Username is mandatory"),
		email: Yup.string().email().required("Email is mandatory"),
		password: Yup.string().password().required("Password is mandatory"),
		confirmPassword: Yup.string()
			.oneOf([Yup.ref("password"), null], "Passwords must match")
			.required(),
		name: Yup.string().required("Name is mandatory"),
		surname: Yup.string().required("Surname is mandatory"),
	});

	const handleSubmit = (values) => {
		const { username, email, password, name, surname } = values;
		setLoading(true);
		api.auth
			.signup(username, email, password, name, surname)
			.then((res) => {
				notify.success(
					`${(res.username, res.email)} You have been successfully registered`
				);
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
				name: "",
				surname: "",
			}}
			validationSchema={SignupSchema}
			onSubmit={(values) => handleSubmit(values)}
		>
			{({ touched, isValid }) => {
				const disableSubmit =
					(!touched.username &&
						!touched.email &&
						!touched.password &&
						!touched.name &&
						!touched.surname &&
						!touched.confirmPassword) ||
					!isValid ||
					loading;
				return (
					<Form>
						<Row>
							{fields.signup.map((props, idx) => {
								return (
									<Col xs={12} sm={6}>
										<InputField
											key={idx}
											id={props.id}
											type={props.type}
											name={props.name}
											placeholder={props.placeholder}
										/>
									</Col>
								);
							})}
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
							Sign In
						</Button>
					</Form>
				);
			}}
		</Formik>
	);
};

export default SignupForm;
