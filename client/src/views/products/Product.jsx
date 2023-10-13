// Imports
import { useEffect, useState } from "react";
import { useLocation, useParams } from "react-router-dom";
import { Spinner, Card } from "react-bootstrap";

// Components
import { InfoCard } from "@components";

// Services
import api from "@services";

// Hooks
import { useNotification } from "@hooks";

const Product = () => {
	const { ean } = useParams();
	const location = useLocation();
	const { state } = location;

	const [product, setProduct] = useState(null);
	const [loading, setLoading] = useState(true);

	const notify = useNotification();

	useEffect(() => {
		if (loading) {
			if (!state?.product) {
				api.product
					.getProductByEAN(ean)
					.then((product) => setProduct(product))
					.catch((err) => {
						setProduct(null)
						if (err.status !== 404)
							notify.error(err.detail ?? err);
					})
					.finally(() => setLoading(false));
			} else {
				setProduct(state.product);
				setLoading(false);
			}
		}
	}, []);

	if (!loading)
		return (
			<>
				{product ? (
					<InfoCard headerTitle={`Product Info`} contentTitle={`Product EAN #${product.ean}`}>
						{[
							{ label: "Name", value: product.name },
							{ label: "Brand", value: product.brand }
						].map((info) => (
							<Card.Text key={`product-${ean}-${info.label}`}>
								<strong>{info.label}</strong>
								<p>{info.value}</p>
							</Card.Text>
						))}
					</InfoCard>
				) : (
					<h3 className={"fw-bold fs-2 text-center"}>
						Product #{ean} not found
					</h3>
				)}
			</>
		);

	return (
		<div className="d-flex justify-content-center align-items-center w-100">
			<Spinner
				animation="border"
				size="xl"
				as="span"
				role="status"
				aria-hidden="true"
				className="me-2"
			/>
			<h2>Loading...</h2>
		</div>
	);
};

export default Product;