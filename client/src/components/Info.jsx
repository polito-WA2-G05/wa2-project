const Info = ({entity}) => {
    return (
        <div className="d-flex align-items-center flex-column bg-light p-3 rounded-3">
            {Object.entries(entity).map(([field, value], idx) => (
                <p key={idx} className={"text-center"}>
                    <b>{field.toUpperCase()}: {" "}</b>
                    {value}
                </p>
            ))}
        </div>
    )
}

export default Info