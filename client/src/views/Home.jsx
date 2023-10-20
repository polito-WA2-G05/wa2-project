// Imports
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { Button, Spinner, Col } from "react-bootstrap";
import { Formik, Form } from "formik";
import * as Yup from "yup";

// Components
import { InputField } from "@components/forms";

// Services
import api from "@services";

// Hooks
import { useNotification } from "@hooks";

const Home = () => {
	const [loading, setLoading] = useState(false);
	const navigate = useNavigate();
	const notify = useNotification();

	const SearchProductSchema = Yup.object().shape({
		ean: Yup.string().required("Product EAN is mandatory"),
	});

	function handleSubmit(values) {
		setLoading(true);
		const { ean } = values;
		api.product
			.getProductByEAN(ean)
			.then((data) =>
				navigate(`/products/${data.ean}`, {
					state: { product: data },
				})
			)
			.catch((err) => notify.error(err.detail ?? err))
			.finally(() => setLoading(false));
	}

	return (
		<div>
			<h1 className="mb-5 fs-1 fw-bold text-center">Search product</h1>
			<Formik
				initialValues={{ ean: "" }}
				validationSchema={SearchProductSchema}
				onSubmit={handleSubmit}
			>
				{({ touched, isValid }) => {
					const disableSubmit = !touched.ean || !isValid || loading;
					return (
						<Form>
							<div className="d-flex flex-column justify-content-center align-items-center">
								<Col xs={12} lg={3}>
									<InputField
										id={"search-ean"}
										type={"text"}
										name={"ean"}
										placeholder={"Product EAN"}
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

export default Home;
