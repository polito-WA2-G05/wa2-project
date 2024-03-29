const Footer = () => {
    const authros = ["Andriano Davide", "Belardo Anna Lisa", "Deluca Andrea", "Tamburo Luca"]

    return (
        <div className={"d-flex flex-column w-100 bottom-0 start-0 bg-light p-4"}>
            <p className={"small text-muted mb-5"}>This project has been developed for the course of Web Application II</p>
            <div className={"d-flex flex-column flex-md-row justify-content-between align-items-md-center"}>
                {authros.map((author, idx) => (
                    <small key={idx} className={"fw-bold"}>{author}</small>
                ))}
            </div>
        </div>
    )
}

export default Footer