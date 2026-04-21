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

// Selección de Color y Variante en /cliente/detalle
document.addEventListener('DOMContentLoaded', function () {
    const section = document.querySelector('section[data-producto-id]');
    if (!section) return;

    const pid = section.getAttribute('data-producto-id');
    const KC  = 'gl-color-'    + pid;
    const KV  = 'gl-variante-' + pid;

    const precioEl   = document.getElementById('precioPrincipal');
    const precioBase = precioEl ? parseFloat(precioEl.dataset.precioBase) : 0;

    function formatCRC(num) {
        return '₡' + new Intl.NumberFormat('es-CR', {
            minimumFractionDigits: 2,
            maximumFractionDigits: 2
        }).format(num);
    }

    function selectColor(btn) {
        document.querySelectorAll('.color-btn').forEach(function (b) {
            b.style.border    = '3px solid transparent';
            b.style.boxShadow = '0 0 0 1px #ccc';
        });
        btn.style.border    = '3px solid #212529';
        btn.style.boxShadow = '0 0 0 3px #212529';
        const label = document.getElementById('detalleColorNombre');
        if (label) label.textContent = btn.dataset.nombre;
        const input = document.getElementById('inputColor');
        if (input) input.value = btn.dataset.id;
        sessionStorage.setItem(KC, btn.dataset.id);
    }

    function deseleccionarVariante() {
        document.querySelectorAll('.variante-btn').forEach(function (b) {
            b.classList.remove('btn-dark');
            b.classList.add('btn-outline-dark');
        });
        const input = document.getElementById('inputVariante');
        if (input) input.value = '';
        sessionStorage.removeItem(KV);
        if (precioEl) precioEl.textContent = formatCRC(precioBase);
    }

    function selectVariante(btn) {
        // Si ya está seleccionado, deseleccionar → precio estándar
        if (btn.classList.contains('btn-dark')) {
            deseleccionarVariante();
            return;
        }
        document.querySelectorAll('.variante-btn').forEach(function (b) {
            b.classList.remove('btn-dark');
            b.classList.add('btn-outline-dark');
        });
        btn.classList.remove('btn-outline-dark');
        btn.classList.add('btn-dark');
        const input = document.getElementById('inputVariante');
        if (input) input.value = btn.dataset.id;
        sessionStorage.setItem(KV, btn.dataset.id);
        if (precioEl) {
            const extra = parseFloat(btn.dataset.precioExtra || 0);
            precioEl.textContent = formatCRC(precioBase + extra);
        }
    }

    document.querySelectorAll('.color-btn').forEach(function (btn) {
        btn.addEventListener('click', function () { selectColor(this); });
    });
    document.querySelectorAll('.variante-btn').forEach(function (btn) {
        btn.addEventListener('click', function () { selectVariante(this); });
    });

    const sc = sessionStorage.getItem(KC);
    if (sc) {
        const cb = document.querySelector('.color-btn[data-id="' + sc + '"]');
        if (cb) selectColor(cb);
    }
    const sv = sessionStorage.getItem(KV);
    if (sv) {
        const vb = document.querySelector('.variante-btn[data-id="' + sv + '"]');
        if (vb) selectVariante(vb);
    }
});

// Agregar producto al carrito y navegar al carrito
function addCart(formulario) {
    var ruta = $(formulario).attr('action') || '/carrito/agregar';
    var datos = $(formulario).serialize();

    $.ajax({
        url: ruta,
        type: 'POST',
        data: datos,
        success: function () {
            window.location.href = '/carrito/listado';
        },
        error: function (xhr) {
            var mensaje = xhr.responseText || 'Error al agregar el producto al carrito.';
            alert(mensaje);
        }
    });
}
