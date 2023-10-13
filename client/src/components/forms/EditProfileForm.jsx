// Imports
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { Spinner, Button, } from "react-bootstrap";
import { Formik, Form } from "formik";
import * as Yup from "yup";

import fields from "./fields";

// Components
import { InputField } from "@components/forms";

// Services
import api from "@services";

// Hooks
import { useSessionStorage, useNotification } from "@hooks";

const EditProfileForm = () => {
	const { session, setSession } = useSessionStorage();
	const [loading, setLoading] = useState(false);

	const navigate = useNavigate();
	const notify = useNotification();

	const EditProfileSchema = Yup.object().shape({
		name: Yup.string().required("Name is mandatory"),
		surname: Yup.string().required("Surname is mandatory"),
	});

	const handleSubmit = (values) => {
		const { name, surname } = values;
		setLoading(true);

		api.profile
			.editProfile(name, surname, session.info.email)
			.then((user) => {
				const updatedSession = {
					...session,
					info: {
						...session.info,
						name: user.name,
						surname: user.surname,
					},
				};
				setSession(updatedSession);
				notify.success("Your info has been successfully edited!");
				navigate("/me", { replace: true });
			})
			.catch((err) => notify.error(err.detail ?? err))
			.finally(() => setLoading(false));
	};

	return (
		<Formik
			initialValues={{ name: session.info.name, surname: session.info.surname }}
			validationSchema={EditProfileSchema}
			onSubmit={(values) => handleSubmit(values)}
		>
			{({ touched, isValid }) => {
				const disableSubmit =
					(!touched.name && !touched.surname) || !isValid || loading;
				return (
					<Form>
						{fields.editProfile.map((props, idx) => (
							<InputField key={idx} {...props} />
						))}
						<div className="d-flex align-items-center justify-content-between">
							<Button
								variant="secondary"
								className="p-2 rounded-3 my-4 me-5 w-100 fw-semibold"
								onClick={() => navigate('/me', { replace: true })}
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

export default EditProfileForm;
