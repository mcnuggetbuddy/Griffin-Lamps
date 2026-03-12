// funcion para hacer un preview de una imagen 
function mostrarImagen(input) {
    if (input.files && input.files[0]) {
        const imagen = input.files[0];
        const maximo = 512 * 1024; //Se limita el tamaño a 512 Kb las imágenes.
        if (imagen.size <= maximo) {
            var lector = new FileReader();
            lector.onload = function (e) {
                $('#blah').attr('src', e.target.result).height(200);
            };
            lector.readAsDataURL(input.files[0]);
        } else {
            alert("La imagen seleccionada es muy grande... no debe superar los 512 Kb!");
        }
    }
}

//Para insertar información en el modal según el registro...
document.addEventListener('DOMContentLoaded', function () {
    const confirmModal = document.getElementById('confirmModal');
    confirmModal.addEventListener('show.bs.modal', function (event) {
        const button = event.relatedTarget;
        document.getElementById('modalId').value = button.getAttribute('data-bs-id');
        document.getElementById('modalNombre').textContent = button.getAttribute('data-bs-nombre');
    });
});

// Modal editar color
document.addEventListener('DOMContentLoaded', function () {
    const editColorModal = document.getElementById('editColorModal');
    if (editColorModal) {
        editColorModal.addEventListener('show.bs.modal', function (event) {
            const btn = event.relatedTarget;
            document.getElementById('colorId').value = btn.getAttribute('data-id');
            document.getElementById('colorNombre').value = btn.getAttribute('data-nombre');
            document.getElementById('colorHex').value = btn.getAttribute('data-hex');
            document.getElementById('colorProductoId').value = btn.getAttribute('data-producto');
        });
    }

    // Modal editar variante
    const editVarianteModal = document.getElementById('editVarianteModal');
    if (editVarianteModal) {
        editVarianteModal.addEventListener('show.bs.modal', function (event) {
            const btn = event.relatedTarget;
            document.getElementById('varianteId').value = btn.getAttribute('data-id');
            document.getElementById('varianteTamano').value = btn.getAttribute('data-tamano');
            document.getElementById('variantePrecio').value = btn.getAttribute('data-precio');
            document.getElementById('varianteProductoId').value = btn.getAttribute('data-producto');
        });
    }
});

//Para quitar toast
setTimeout(() => {
    document.querySelectorAll('.toast').forEach(t => t.classList.remove('show'));
}, 4000);
