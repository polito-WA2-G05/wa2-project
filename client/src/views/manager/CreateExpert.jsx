// Imports
import { useContext, useEffect, useState } from "react";
import { Col } from "react-bootstrap";

// Components
import { Loader } from "@components/layout";
import { CreateExpertForm } from "@components/forms";

import api from "@services"
import { SessionContext } from "@contexts";

const CreateExpert = () => {
	const [loading, setLoading] = useState(true)
	const [specializations, setSpecializations] = useState([])

	const {onError} = useContext(SessionContext)

	useEffect(() => {
		if (loading)
			api.ticket.getSpecializations()
				.then((specializations) => setSpecializations(specializations))
				.catch(onError)
				.finally(() => setLoading(false))
	}, []) // eslint-disable-line


	if (!loading)
		return <Col xs={12} lg={6}>
			<h1 className="fw-bold my-5 text-center">Create Expert</h1>
			<CreateExpertForm specializations={specializations} />
		</Col>


	return <Loader />
}

export default CreateExpert;
